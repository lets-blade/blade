/*
 * Copyright (c) 2014 Kevin Sawicki <kevinsawicki@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */
package com.blade.kit.http;

import com.blade.kit.Assert;
import com.blade.kit.EncodeKit;

import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.nio.CharBuffer;
import java.security.AccessController;
import java.security.GeneralSecurityException;
import java.security.PrivilegedAction;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.GZIPInputStream;

import static java.net.HttpURLConnection.*;
import static java.net.Proxy.Type.HTTP;

/**
 * Http请求类
 *
 * @author <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.0
 */
public class HttpRequest {

	private static final String BOUNDARY = "00content0boundary00";

	private static final String CONTENT_TYPE_MULTIPART = "multipart/form-data; boundary=" + BOUNDARY;

	private static final String CRLF = "\r\n";

	private static final String[] EMPTY_STRINGS = new String[0];

	private static SSLSocketFactory TRUSTED_FACTORY;

	private static HostnameVerifier TRUSTED_VERIFIER;

	private static ConnectionFactory CONNECTION_FACTORY = ConnectionFactory.DEFAULT;

	public static String getValidCharset(final String charset) {
		if (charset != null && charset.length() > 0)
			return charset;
		else
			return Header.CHARSET_UTF8;
	}

	/**
	 * @return 返回SSL套接字工厂
	 * @throws HttpRequestException
	 */
	private static SSLSocketFactory getTrustedFactory() throws HttpRequestException {
		if (TRUSTED_FACTORY == null) {
			final TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {

				public X509Certificate[] getAcceptedIssuers() {
					return new X509Certificate[0];
				}

				public void checkClientTrusted(X509Certificate[] chain, String authType) {
					// Intentionally left blank
				}

				public void checkServerTrusted(X509Certificate[] chain, String authType) {
					// Intentionally left blank
				}
			} };
			try {
				SSLContext context = SSLContext.getInstance("TLS");
				context.init(null, trustAllCerts, new SecureRandom());
				TRUSTED_FACTORY = context.getSocketFactory();
			} catch (GeneralSecurityException e) {
				IOException ioException = new IOException("Security exception configuring SSL context");
				ioException.initCause(e);
				throw new HttpRequestException(ioException);
			}
		}

		return TRUSTED_FACTORY;
	}

	private static HostnameVerifier getTrustedVerifier() {
		if (TRUSTED_VERIFIER == null)
			TRUSTED_VERIFIER = new HostnameVerifier() {

				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};

		return TRUSTED_VERIFIER;
	}

	private static StringBuilder addPathSeparator(final String baseUrl, final StringBuilder result) {
		// Add trailing slash if the base URL doesn't have any path segments.
		//
		// The following test is checking for the last slash not being part of
		// the protocol to host separator: '://'.
		if (baseUrl.indexOf(':') + 2 == baseUrl.lastIndexOf('/'))
			result.append('/');
		return result;
	}

	private static StringBuilder addParamPrefix(final String baseUrl, final StringBuilder result) {
		// Add '?' if missing and add '&' if params already exist in base url
		final int queryStart = baseUrl.indexOf('?');
		final int lastChar = result.length() - 1;
		if (queryStart == -1)
			result.append('?');
		else if (queryStart < lastChar && baseUrl.charAt(lastChar) != '&')
			result.append('&');
		return result;
	}

	private static StringBuilder addParam(final Object key, Object value, final StringBuilder result) {
		if (value != null && value.getClass().isArray())
			value = arrayToList(value);

		if (value instanceof Iterable<?>) {
			Iterator<?> iterator = ((Iterable<?>) value).iterator();
			while (iterator.hasNext()) {
				result.append(key);
				result.append("[]=");
				Object element = iterator.next();
				if (element != null)
					result.append(element);
				if (iterator.hasNext())
					result.append("&");
			}
		} else {
			result.append(key);
			result.append("=");
			if (value != null)
				result.append(value);
		}

		return result;
	}

	/**
	 * 设置一个ConnectionFactory，用于创建新的请求
	 */
	public static void setConnectionFactory(final ConnectionFactory connectionFactory) {
		if (connectionFactory == null)
			CONNECTION_FACTORY = ConnectionFactory.DEFAULT;
		else
			CONNECTION_FACTORY = connectionFactory;
	}

	/**
	 * 操作执行一个回调处理完成后和处理嵌套的异常
	 *
	 * @param <V>
	 */
	protected static abstract class Operation<V> implements Callable<V> {

		/**
		 * 执行操作
		 *
		 * @return result
		 * @throws HttpRequestException
		 * @throws IOException
		 */
		protected abstract V run() throws HttpRequestException, IOException;

		/**
		 * 操作完成回调
		 *
		 * @throws IOException
		 */
		protected abstract void done() throws IOException;

		public V call() throws HttpRequestException {
			boolean thrown = false;
			try {
				return run();
			} catch (HttpRequestException e) {
				thrown = true;
				throw e;
			} catch (IOException e) {
				thrown = true;
				throw new HttpRequestException(e);
			} finally {
				try {
					done();
				} catch (IOException e) {
					if (!thrown)
						throw new HttpRequestException(e);
				}
			}
		}
	}

	/**
	 * 确保Closeable类关闭,使用适当的异常处理
	 *
	 * @param <V>
	 */
	protected static abstract class CloseOperation<V> extends Operation<V> {

		private final Closeable closeable;

		private final boolean ignoreCloseExceptions;

		/**
		 * 创建一个关闭操作
		 *
		 * @param closeable
		 * @param ignoreCloseExceptions
		 */
		protected CloseOperation(final Closeable closeable, final boolean ignoreCloseExceptions) {
			this.closeable = closeable;
			this.ignoreCloseExceptions = ignoreCloseExceptions;
		}

		@Override
		protected void done() throws IOException {
			if (closeable instanceof Flushable)
				((Flushable) closeable).flush();
			if (ignoreCloseExceptions)
				try {
					closeable.close();
				} catch (IOException e) {
					// Ignored
				}
			else
				closeable.close();
		}
	}

	/**
	 * Class that and ensures a {@link Flushable} gets flushed with proper
	 * exception handling.
	 *
	 * @param <V>
	 */
	protected static abstract class FlushOperation<V> extends Operation<V> {

		private final Flushable flushable;

		/**
		 * Create flush operation
		 *
		 * @param flushable
		 */
		protected FlushOperation(final Flushable flushable) {
			this.flushable = flushable;
		}

		@Override
		protected void done() throws IOException {
			flushable.flush();
		}
	}

	/**
	 * 表示任何类型的数组的对象列表,让我们可以很容易地遍历
	 * 
	 * @param array
	 *            of elements
	 * @return list with the same elements
	 */
	private static List<Object> arrayToList(final Object array) {
		if (array instanceof Object[])
			return Arrays.asList((Object[]) array);

		List<Object> result = new ArrayList<Object>();
		// Arrays of the primitive types can't be cast to array of Object, so
		// this:
		if (array instanceof int[])
			for (int value : (int[]) array)
				result.add(value);
		else if (array instanceof boolean[])
			for (boolean value : (boolean[]) array)
				result.add(value);
		else if (array instanceof long[])
			for (long value : (long[]) array)
				result.add(value);
		else if (array instanceof float[])
			for (float value : (float[]) array)
				result.add(value);
		else if (array instanceof double[])
			for (double value : (double[]) array)
				result.add(value);
		else if (array instanceof short[])
			for (short value : (short[]) array)
				result.add(value);
		else if (array instanceof byte[])
			for (byte value : (byte[]) array)
				result.add(value);
		else if (array instanceof char[])
			for (char value : (char[]) array)
				result.add(value);
		return result;
	}

	/**
	 * Encode the given URL as an ASCII {@link String}
	 * <p>
	 * This method ensures the path and query segments of the URL are properly
	 * encoded such as ' ' characters being encoded to '%20' or any UTF-8
	 * characters that are non-ASCII. No encoding of URLs is done by default by
	 * the {@link HttpRequest} constructors and so if URL encoding is needed
	 * this method should be called before calling the {@link HttpRequest}
	 * constructor.
	 *
	 * @param url
	 * @return encoded URL
	 * @throws HttpRequestException
	 */
	public static String encode(final String url) throws HttpRequestException {
		URL parsed;
		try {
			parsed = new URL(url.toString());
		} catch (IOException e) {
			throw new HttpRequestException(e);
		}

		String host = parsed.getHost();
		int port = parsed.getPort();
		if (port != -1)
			host = host + ':' + Integer.toString(port);

		try {
			String encoded = new URI(parsed.getProtocol(), host, parsed.getPath(), parsed.getQuery(), null)
					.toASCIIString();
			int paramsStart = encoded.indexOf('?');
			if (paramsStart > 0 && paramsStart + 1 < encoded.length())
				encoded = encoded.substring(0, paramsStart + 1)
						+ encoded.substring(paramsStart + 1).replace("+", "%2B");
			return encoded;
		} catch (URISyntaxException e) {
			IOException io = new IOException("Parsing URI failed");
			io.initCause(e);
			throw new HttpRequestException(io);
		}
	}

	/**
	 * Append given map as query parameters to the base URL
	 * <p>
	 * Each map entry's key will be a parameter name and the value's
	 * {@link Object#toString()} will be the parameter value.
	 *
	 * @param url
	 * @param params
	 * @return URL with appended query params
	 */
	public static String append(final String url, final Map<?, ?> params) {
		final String baseUrl = url.toString();
		if (params == null || params.isEmpty())
			return baseUrl;

		final StringBuilder result = new StringBuilder(baseUrl);

		addPathSeparator(baseUrl, result);
		addParamPrefix(baseUrl, result);

		Entry<?, ?> entry;
		Iterator<?> iterator = params.entrySet().iterator();
		entry = (Entry<?, ?>) iterator.next();
		addParam(entry.getKey().toString(), entry.getValue(), result);

		while (iterator.hasNext()) {
			result.append('&');
			entry = (Entry<?, ?>) iterator.next();
			addParam(entry.getKey().toString(), entry.getValue(), result);
		}

		return result.toString();
	}

	/**
	 * Append given name/value pairs as query parameters to the base URL
	 * <p>
	 * The params argument is interpreted as a sequence of name/value pairs so
	 * the given number of params must be divisible by 2.
	 *
	 * @param url
	 * @param params
	 *            name/value pairs
	 * @return URL with appended query params
	 */
	public static String append(final String url, final Object... params) {
		final String baseUrl = url.toString();
		if (params == null || params.length == 0)
			return baseUrl;

		if (params.length % 2 != 0)
			throw new IllegalArgumentException("Must specify an even number of parameter names/values");

		final StringBuilder result = new StringBuilder(baseUrl);

		addPathSeparator(baseUrl, result);
		addParamPrefix(baseUrl, result);

		addParam(params[0], params[1], result);

		for (int i = 2; i < params.length; i += 2) {
			result.append('&');
			addParam(params[i], params[i + 1], result);
		}

		return result.toString();
	}

	/**
	 * Start a 'GET' request to the given URL
	 *
	 * @param url
	 * @return request
	 * @throws HttpRequestException
	 */
	public static HttpRequest get(final String url) throws HttpRequestException {
		return new HttpRequest(url, MethodType.GET);
	}

	/**
	 * Start a 'GET' request to the given URL
	 *
	 * @param url
	 * @return request
	 * @throws HttpRequestException
	 */
	public static HttpRequest get(final URL url) throws HttpRequestException {
		return new HttpRequest(url, MethodType.GET);
	}

	/**
	 * Start a 'GET' request to the given URL along with the query params
	 *
	 * @param baseUrl
	 * @param params
	 *            The query parameters to include as part of the baseUrl
	 * @param encode
	 *            true to encode the full URL
	 *
	 * @return request
	 */
	public static HttpRequest get(final String baseUrl, final Map<?, ?> params, final boolean encode) {
		String url = append(baseUrl, params);
		return get(encode ? encode(url) : url);
	}

	/**
	 * Start a 'GET' request to the given URL along with the query params
	 *
	 * @param baseUrl
	 * @param encode
	 *            true to encode the full URL
	 * @param params
	 *            the name/value query parameter pairs to include as part of the
	 *            baseUrl
	 *
	 * @return request
	 */
	public static HttpRequest get(final String baseUrl, final boolean encode, final Object... params) {
		String url = append(baseUrl, params);
		return get(encode ? encode(url) : url);
	}

	/**
	 * Start a 'POST' request to the given URL
	 *
	 * @param url
	 * @return request
	 * @throws HttpRequestException
	 */
	public static HttpRequest post(final String url) throws HttpRequestException {
		return new HttpRequest(url, MethodType.POST);
	}

	/**
	 * Start a 'POST' request to the given URL
	 *
	 * @param url
	 * @return request
	 * @throws HttpRequestException
	 */
	public static HttpRequest post(final URL url) throws HttpRequestException {
		return new HttpRequest(url, MethodType.POST);
	}

	/**
	 * Start a 'POST' request to the given URL along with the query params
	 *
	 * @param baseUrl
	 * @param params
	 *            the query parameters to include as part of the baseUrl
	 * @param encode
	 *            true to encode the full URL
	 *
	 * @see #append(String, Map)
	 * @see #encode(String)
	 *
	 * @return request
	 */
	public static HttpRequest post(final String baseUrl, final Map<?, ?> params, final boolean encode) {
		String url = append(baseUrl, params);
		return post(encode ? encode(url) : url);
	}

	/**
	 * Start a 'POST' request to the given URL along with the query params
	 *
	 * @param baseUrl
	 * @param encode
	 *            true to encode the full URL
	 * @param params
	 *            the name/value query parameter pairs to include as part of the
	 *            baseUrl
	 *
	 * @see #append(String, Object...)
	 * @see #encode(String)
	 *
	 * @return request
	 */
	public static HttpRequest post(final String baseUrl, final boolean encode, final Object... params) {
		String url = append(baseUrl, params);
		return post(encode ? encode(url) : url);
	}

	/**
	 * Start a 'PUT' request to the given URL
	 *
	 * @param url
	 * @return request
	 * @throws HttpRequestException
	 */
	public static HttpRequest put(final String url) throws HttpRequestException {
		return new HttpRequest(url, MethodType.PUT);
	}

	/**
	 * Start a 'PUT' request to the given URL
	 *
	 * @param url
	 * @return request
	 * @throws HttpRequestException
	 */
	public static HttpRequest put(final URL url) throws HttpRequestException {
		return new HttpRequest(url, MethodType.PUT);
	}

	/**
	 * Start a 'PUT' request to the given URL along with the query params
	 *
	 * @param baseUrl
	 * @param params
	 *            the query parameters to include as part of the baseUrl
	 * @param encode
	 *            true to encode the full URL
	 *
	 * @see #append(String, Map)
	 * @see #encode(String)
	 *
	 * @return request
	 */
	public static HttpRequest put(final String baseUrl, final Map<?, ?> params, final boolean encode) {
		String url = append(baseUrl, params);
		return put(encode ? encode(url) : url);
	}

	/**
	 * Start a 'PUT' request to the given URL along with the query params
	 *
	 * @param baseUrl
	 * @param encode
	 *            true to encode the full URL
	 * @param params
	 *            the name/value query parameter pairs to include as part of the
	 *            baseUrl
	 *
	 * @see #append(String, Object...)
	 * @see #encode(String)
	 *
	 * @return request
	 */
	public static HttpRequest put(final String baseUrl, final boolean encode, final Object... params) {
		String url = append(baseUrl, params);
		return put(encode ? encode(url) : url);
	}

	/**
	 * Start a 'DELETE' request to the given URL
	 *
	 * @param url
	 * @return request
	 * @throws HttpRequestException
	 */
	public static HttpRequest delete(final String url) throws HttpRequestException {
		return new HttpRequest(url, MethodType.DELETE);
	}

	/**
	 * Start a 'DELETE' request to the given URL
	 *
	 * @param url
	 * @return request
	 * @throws HttpRequestException
	 */
	public static HttpRequest delete(final URL url) throws HttpRequestException {
		return new HttpRequest(url, MethodType.DELETE);
	}

	/**
	 * Start a 'DELETE' request to the given URL along with the query params
	 *
	 * @param baseUrl
	 * @param params
	 *            The query parameters to include as part of the baseUrl
	 * @param encode
	 *            true to encode the full URL
	 *
	 * @see #append(String, Map)
	 * @see #encode(String)
	 *
	 * @return request
	 */
	public static HttpRequest delete(final String baseUrl, final Map<?, ?> params, final boolean encode) {
		String url = append(baseUrl, params);
		return delete(encode ? encode(url) : url);
	}

	/**
	 * Start a 'DELETE' request to the given URL along with the query params
	 *
	 * @param baseUrl
	 * @param encode
	 *            true to encode the full URL
	 * @param params
	 *            the name/value query parameter pairs to include as part of the
	 *            baseUrl
	 *
	 * @see #append(String, Object...)
	 * @see #encode(String)
	 *
	 * @return request
	 */
	public static HttpRequest delete(final String baseUrl, final boolean encode, final Object... params) {
		String url = append(baseUrl, params);
		return delete(encode ? encode(url) : url);
	}

	/**
	 * Start a 'HEAD' request to the given URL
	 *
	 * @param url
	 * @return request
	 * @throws HttpRequestException
	 */
	public static HttpRequest head(final String url) throws HttpRequestException {
		return new HttpRequest(url, MethodType.HEAD);
	}

	/**
	 * Start a 'HEAD' request to the given URL
	 *
	 * @param url
	 * @return request
	 * @throws HttpRequestException
	 */
	public static HttpRequest head(final URL url) throws HttpRequestException {
		return new HttpRequest(url, MethodType.HEAD);
	}

	/**
	 * Start a 'HEAD' request to the given URL along with the query params
	 *
	 * @param baseUrl
	 * @param params
	 *            The query parameters to include as part of the baseUrl
	 * @param encode
	 *            true to encode the full URL
	 *
	 * @see #append(String, Map)
	 * @see #encode(String)
	 *
	 * @return request
	 */
	public static HttpRequest head(final String baseUrl, final Map<?, ?> params, final boolean encode) {
		String url = append(baseUrl, params);
		return head(encode ? encode(url) : url);
	}

	/**
	 * Start a 'GET' request to the given URL along with the query params
	 *
	 * @param baseUrl
	 * @param encode
	 *            true to encode the full URL
	 * @param params
	 *            the name/value query parameter pairs to include as part of the
	 *            baseUrl
	 *
	 * @see #append(String, Object...)
	 * @see #encode(String)
	 *
	 * @return request
	 */
	public static HttpRequest head(final String baseUrl, final boolean encode, final Object... params) {
		String url = append(baseUrl, params);
		return head(encode ? encode(url) : url);
	}

	/**
	 * Start an 'OPTIONS' request to the given URL
	 *
	 * @param url
	 * @return request
	 * @throws HttpRequestException
	 */
	public static HttpRequest options(final String url) throws HttpRequestException {
		return new HttpRequest(url, MethodType.OPTIONS);
	}

	/**
	 * Start an 'OPTIONS' request to the given URL
	 *
	 * @param url
	 * @return request
	 * @throws HttpRequestException
	 */
	public static HttpRequest options(final URL url) throws HttpRequestException {
		return new HttpRequest(url, MethodType.OPTIONS);
	}

	/**
	 * Start a 'TRACE' request to the given URL
	 *
	 * @param url
	 * @return request
	 * @throws HttpRequestException
	 */
	public static HttpRequest trace(final String url) throws HttpRequestException {
		return new HttpRequest(url, MethodType.TRACE);
	}

	/**
	 * Start a 'TRACE' request to the given URL
	 *
	 * @param url
	 * @return request
	 * @throws HttpRequestException
	 */
	public static HttpRequest trace(final URL url) throws HttpRequestException {
		return new HttpRequest(url, MethodType.TRACE);
	}

	/**
	 * Set the 'http.keepAlive' property to the given value.
	 * <p>
	 * This setting will apply to all requests.
	 *
	 * @param keepAlive
	 */
	public static void keepAlive(final boolean keepAlive) {
		setProperty("http.keepAlive", Boolean.toString(keepAlive));
	}

	/**
	 * Set the 'http.maxConnections' property to the given value.
	 * <p>
	 * This setting will apply to all requests.
	 *
	 * @param maxConnections
	 */
	public static void maxConnections(final int maxConnections) {
		setProperty("http.maxConnections", Integer.toString(maxConnections));
	}

	/**
	 * Set the 'http.proxyHost' and 'https.proxyHost' properties to the given
	 * host value.
	 * <p>
	 * This setting will apply to all requests.
	 *
	 * @param host
	 */
	public static void proxyHost(final String host) {
		setProperty("http.proxyHost", host);
		setProperty("https.proxyHost", host);
	}

	/**
	 * Set the 'http.proxyPort' and 'https.proxyPort' properties to the given
	 * port number.
	 * <p>
	 * This setting will apply to all requests.
	 *
	 * @param port
	 */
	public static void proxyPort(final int port) {
		final String portValue = Integer.toString(port);
		setProperty("http.proxyPort", portValue);
		setProperty("https.proxyPort", portValue);
	}

	/**
	 * Set the 'http.nonProxyHosts' property to the given host values.
	 * <p>
	 * Hosts will be separated by a '|' character.
	 * <p>
	 * This setting will apply to all requests.
	 *
	 * @param hosts
	 */
	public static void nonProxyHosts(final String... hosts) {
		if (hosts != null && hosts.length > 0) {
			StringBuilder separated = new StringBuilder();
			int last = hosts.length - 1;
			for (int i = 0; i < last; i++)
				separated.append(hosts[i]).append('|');
			separated.append(hosts[last]);
			setProperty("http.nonProxyHosts", separated.toString());
		} else
			setProperty("http.nonProxyHosts", null);
	}

	/**
	 * Set property to given value.
	 * <p>
	 * Specifying a null value will cause the property to be cleared
	 *
	 * @param name
	 * @param value
	 * @return previous value
	 */
	private static String setProperty(final String name, final String value) {
		final PrivilegedAction<String> action;
		if (value != null)
			action = new PrivilegedAction<String>() {

				public String run() {
					return System.setProperty(name, value);
				}
			};
		else
			action = new PrivilegedAction<String>() {

				public String run() {
					return System.clearProperty(name);
				}
			};
		return AccessController.doPrivileged(action);
	}

	private HttpURLConnection connection = null;

	private final URL url;

	private final String requestMethod;

	private RequestOutputStream output;

	private boolean multipart;

	private boolean form;

	private boolean ignoreCloseExceptions = true;

	private boolean uncompress = false;

	private int bufferSize = 8192;

	private long totalSize = -1;

	private long totalWritten = 0;

	private String httpProxyHost;

	private int httpProxyPort;

	private UploadProgress progress = UploadProgress.DEFAULT;

	private Map<String, String> cookies;
	
	/**
	 * Create HTTP connection wrapper
	 *
	 * @param url
	 *            Remote resource URL.
	 * @param method
	 *            HTTP request method (e.g., "GET", "POST").
	 * @throws HttpRequestException
	 */
	public HttpRequest(final String url, final String method) throws HttpRequestException {
		try {
			this.url = new URL(url.toString());
		} catch (MalformedURLException e) {
			throw new HttpRequestException(e);
		}
		this.requestMethod = method;
		this.cookies = new LinkedHashMap<String, String>();
	}

	/**
	 * Create HTTP connection wrapper
	 *
	 * @param url
	 *            Remote resource URL.
	 * @param method
	 *            HTTP request method (e.g., "GET", "POST").
	 * @throws HttpRequestException
	 */
	public HttpRequest(final URL url, final String method) throws HttpRequestException {
		this.url = url;
		this.requestMethod = method;
		this.cookies = new LinkedHashMap<String, String>();
	}

	private Proxy createProxy() {
		return new Proxy(HTTP, new InetSocketAddress(httpProxyHost, httpProxyPort));
	}

	private HttpURLConnection createConnection() {
		try {
			final HttpURLConnection connection;
			if (httpProxyHost != null)
				connection = CONNECTION_FACTORY.create(url, createProxy());
			else
				connection = CONNECTION_FACTORY.create(url);
			connection.setRequestMethod(requestMethod);
			return connection;
		} catch (IOException e) {
			throw new HttpRequestException(e);
		}
	}

	@Override
	public String toString() {
		return method() + ' ' + url();
	}

	/**
	 * Get underlying connection
	 *
	 * @return connection
	 */
	public HttpURLConnection getConnection() {
		if (connection == null)
			connection = createConnection();
		return connection;
	}

	/**
	 * Set whether or not to ignore exceptions that occur from calling
	 * {@link Closeable#close()}
	 * <p>
	 * The default value of this setting is <code>true</code>
	 *
	 * @param ignore
	 * @return this request
	 */
	public HttpRequest ignoreCloseExceptions(final boolean ignore) {
		ignoreCloseExceptions = ignore;
		return this;
	}

	/**
	 * Get whether or not exceptions thrown by {@link Closeable#close()} are
	 * ignored
	 *
	 * @return true if ignoring, false if throwing
	 */
	public boolean ignoreCloseExceptions() {
		return ignoreCloseExceptions;
	}

	/**
	 * Get the status code of the response
	 *
	 * @return the response code
	 * @throws HttpRequestException
	 */
	public int code() throws HttpRequestException {
		try {
			closeOutput();
			return getConnection().getResponseCode();
		} catch (IOException e) {
			throw new HttpRequestException(e);
		}
	}

	/**
	 * Set the value of the given {@link AtomicInteger} to the status code of
	 * the response
	 *
	 * @param output
	 * @return this request
	 * @throws HttpRequestException
	 */
	public HttpRequest code(final AtomicInteger output) throws HttpRequestException {
		output.set(code());
		return this;
	}
	
	/**
	 * Is the response code a 200 OK?
	 *
	 * @return true if 200, false otherwise
	 * @throws HttpRequestException
	 */
	public boolean ok() throws HttpRequestException {
		return HTTP_OK == code();
	}

	/**
	 * Is the response code a 201 Created?
	 *
	 * @return true if 201, false otherwise
	 * @throws HttpRequestException
	 */
	public boolean created() throws HttpRequestException {
		return HTTP_CREATED == code();
	}

	/**
	 * Is the response code a 204 No Content?
	 *
	 * @return true if 204, false otherwise
	 * @throws HttpRequestException
	 */
	public boolean noContent() throws HttpRequestException {
		return HTTP_NO_CONTENT == code();
	}

	/**
	 * Is the response code a 500 Internal Server Error?
	 *
	 * @return true if 500, false otherwise
	 * @throws HttpRequestException
	 */
	public boolean serverError() throws HttpRequestException {
		return HTTP_INTERNAL_ERROR == code();
	}

	/**
	 * Is the response code a 400 Bad Request?
	 *
	 * @return true if 400, false otherwise
	 * @throws HttpRequestException
	 */
	public boolean badRequest() throws HttpRequestException {
		return HTTP_BAD_REQUEST == code();
	}

	/**
	 * Is the response code a 404 Not Found?
	 *
	 * @return true if 404, false otherwise
	 * @throws HttpRequestException
	 */
	public boolean notFound() throws HttpRequestException {
		return HTTP_NOT_FOUND == code();
	}

	/**
	 * Is the response code a 304 Not Modified?
	 *
	 * @return true if 304, false otherwise
	 * @throws HttpRequestException
	 */
	public boolean notModified() throws HttpRequestException {
		return HTTP_NOT_MODIFIED == code();
	}

	/**
	 * Get status message of the response
	 *
	 * @return message
	 * @throws HttpRequestException
	 */
	public String message() throws HttpRequestException {
		try {
			closeOutput();
			return getConnection().getResponseMessage();
		} catch (IOException e) {
			throw new HttpRequestException(e);
		}
	}

	/**
	 * Disconnect the connection
	 *
	 * @return this request
	 */
	public HttpRequest disconnect() {
		getConnection().disconnect();
		return this;
	}

	/**
	 * Set chunked streaming mode to the given size
	 *
	 * @param size
	 * @return this request
	 */
	public HttpRequest chunk(final int size) {
		getConnection().setChunkedStreamingMode(size);
		return this;
	}

	/**
	 * Set the size used when buffering and copying between streams
	 * <p>
	 * This size is also used for send and receive buffers created for both char
	 * and byte arrays
	 * <p>
	 * The default buffer size is 8,192 bytes
	 *
	 * @param size
	 * @return this request
	 */
	public HttpRequest bufferSize(final int size) {
		if (size < 1)
			throw new IllegalArgumentException("Size must be greater than zero");
		bufferSize = size;
		return this;
	}

	/**
	 * Get the configured buffer size
	 * <p>
	 * The default buffer size is 8,192 bytes
	 *
	 * @return buffer size
	 */
	public int bufferSize() {
		return bufferSize;
	}

	/**
	 * Set whether or not the response body should be automatically uncompressed
	 * when read from.
	 * <p>
	 * This will only affect requests that have the 'Content-Encoding' response
	 * header set to 'gzip'.
	 * <p>
	 * This causes all receive methods to use a {@link GZIPInputStream} when
	 * applicable so that higher level streams and readers can read the data
	 * uncompressed.
	 * <p>
	 * Setting this option does not cause any request headers to be set
	 * automatically so {@link #acceptGzipEncoding()} should be used in
	 * conjunction with this setting to tell the server to gzip the response.
	 *
	 * @param uncompress
	 * @return this request
	 */
	public HttpRequest uncompress(final boolean uncompress) {
		this.uncompress = uncompress;
		return this;
	}

	/**
	 * Create byte array output stream
	 *
	 * @return stream
	 */
	protected ByteArrayOutputStream byteStream() {
		final int size = contentLength();
		if (size > 0)
			return new ByteArrayOutputStream(size);
		else
			return new ByteArrayOutputStream();
	}

	/**
	 * Get response as {@link String} in given character set
	 * <p>
	 * This will fall back to using the UTF-8 character set if the given charset
	 * is null
	 *
	 * @param charset
	 * @return string
	 * @throws HttpRequestException
	 */
	public String body(final String charset) throws HttpRequestException {
		final ByteArrayOutputStream output = byteStream();
		try {
			copy(buffer(), output);
			return output.toString(getValidCharset(charset));
		} catch (IOException e) {
			throw new HttpRequestException(e);
		}
	}

	/**
	 * Get response as {@link String} using character set returned from
	 * {@link #charset()}
	 *
	 * @return string
	 * @throws HttpRequestException
	 */
	public String body() throws HttpRequestException {
		return body(charset());
	}

	/**
	 * Get the response body as a {@link String} and set it as the value of the
	 * given reference.
	 *
	 * @param output
	 * @return this request
	 * @throws HttpRequestException
	 */
	public HttpRequest body(final AtomicReference<String> output) throws HttpRequestException {
		output.set(body());
		return this;
	}

	/**
	 * Get the response body as a {@link String} and set it as the value of the
	 * given reference.
	 *
	 * @param output
	 * @param charset
	 * @return this request
	 * @throws HttpRequestException
	 */
	public HttpRequest body(final AtomicReference<String> output, final String charset) throws HttpRequestException {
		output.set(body(charset));
		return this;
	}

	/**
	 * Is the response body empty?
	 *
	 * @return true if the Content-Length response header is 0, false otherwise
	 * @throws HttpRequestException
	 */
	public boolean isBodyEmpty() throws HttpRequestException {
		return contentLength() == 0;
	}

	/**
	 * Get response as byte array
	 *
	 * @return byte array
	 * @throws HttpRequestException
	 */
	public byte[] bytes() throws HttpRequestException {
		final ByteArrayOutputStream output = byteStream();
		try {
			copy(buffer(), output);
		} catch (IOException e) {
			throw new HttpRequestException(e);
		}
		return output.toByteArray();
	}

	/**
	 * Get response in a buffered stream
	 *
	 * @see #bufferSize(int)
	 * @return stream
	 * @throws HttpRequestException
	 */
	public BufferedInputStream buffer() throws HttpRequestException {
		return new BufferedInputStream(stream(), bufferSize);
	}

	/**
	 * Get stream to response body
	 *
	 * @return stream
	 * @throws HttpRequestException
	 */
	public InputStream stream() throws HttpRequestException {
		InputStream stream;
		if (code() < HTTP_BAD_REQUEST)
			try {
				stream = getConnection().getInputStream();
			} catch (IOException e) {
				throw new HttpRequestException(e);
			}
		else {
			stream = getConnection().getErrorStream();
			if (stream == null)
				try {
					stream = getConnection().getInputStream();
				} catch (IOException e) {
					if (contentLength() > 0)
						throw new HttpRequestException(e);
					else
						stream = new ByteArrayInputStream(new byte[0]);
				}
		}

		if (!uncompress || !Header.ENCODING_GZIP.equals(contentEncoding()))
			return stream;
		else
			try {
				return new GZIPInputStream(stream);
			} catch (IOException e) {
				throw new HttpRequestException(e);
			}
	}

	/**
	 * Get reader to response body using given character set.
	 * <p>
	 * This will fall back to using the UTF-8 character set if the given charset
	 * is null
	 *
	 * @param charset
	 * @return reader
	 * @throws HttpRequestException
	 */
	public InputStreamReader reader(final String charset) throws HttpRequestException {
		try {
			return new InputStreamReader(stream(), getValidCharset(charset));
		} catch (UnsupportedEncodingException e) {
			throw new HttpRequestException(e);
		}
	}

	/**
	 * Get reader to response body using the character set returned from
	 * {@link #charset()}
	 *
	 * @return reader
	 * @throws HttpRequestException
	 */
	public InputStreamReader reader() throws HttpRequestException {
		return reader(charset());
	}

	/**
	 * Get buffered reader to response body using the given character set r and
	 * the configured buffer size
	 *
	 *
	 * @see #bufferSize(int)
	 * @param charset
	 * @return reader
	 * @throws HttpRequestException
	 */
	public BufferedReader bufferedReader(final String charset) throws HttpRequestException {
		return new BufferedReader(reader(charset), bufferSize);
	}

	/**
	 * Get buffered reader to response body using the character set returned
	 * from {@link #charset()} and the configured buffer size
	 *
	 * @see #bufferSize(int)
	 * @return reader
	 * @throws HttpRequestException
	 */
	public BufferedReader bufferedReader() throws HttpRequestException {
		return bufferedReader(charset());
	}

	/**
	 * Stream response body to file
	 *
	 * @param file
	 * @return this request
	 * @throws HttpRequestException
	 */
	public HttpRequest receive(final File file) throws HttpRequestException {
		final OutputStream output;
		try {
			output = new BufferedOutputStream(new FileOutputStream(file), bufferSize);
		} catch (FileNotFoundException e) {
			throw new HttpRequestException(e);
		}
		return new CloseOperation<HttpRequest>(output, ignoreCloseExceptions) {

			@Override
			protected HttpRequest run() throws HttpRequestException, IOException {
				return receive(output);
			}
		}.call();
	}

	/**
	 * Stream response to given output stream
	 *
	 * @param output
	 * @return this request
	 * @throws HttpRequestException
	 */
	public HttpRequest receive(final OutputStream output) throws HttpRequestException {
		try {
			return copy(buffer(), output);
		} catch (IOException e) {
			throw new HttpRequestException(e);
		}
	}

	/**
	 * Stream response to given print stream
	 *
	 * @param output
	 * @return this request
	 * @throws HttpRequestException
	 */
	public HttpRequest receive(final PrintStream output) throws HttpRequestException {
		return receive((OutputStream) output);
	}

	/**
	 * Receive response into the given appendable
	 *
	 * @param appendable
	 * @return this request
	 * @throws HttpRequestException
	 */
	public HttpRequest receive(final Appendable appendable) throws HttpRequestException {
		final BufferedReader reader = bufferedReader();
		return new CloseOperation<HttpRequest>(reader, ignoreCloseExceptions) {

			@Override
			public HttpRequest run() throws IOException {
				final CharBuffer buffer = CharBuffer.allocate(bufferSize);
				int read;
				while ((read = reader.read(buffer)) != -1) {
					buffer.rewind();
					appendable.append(buffer, 0, read);
					buffer.rewind();
				}
				return HttpRequest.this;
			}
		}.call();
	}

	/**
	 * Receive response into the given writer
	 *
	 * @param writer
	 * @return this request
	 * @throws HttpRequestException
	 */
	public HttpRequest receive(final Writer writer) throws HttpRequestException {
		final BufferedReader reader = bufferedReader();
		return new CloseOperation<HttpRequest>(reader, ignoreCloseExceptions) {

			@Override
			public HttpRequest run() throws IOException {
				return copy(reader, writer);
			}
		}.call();
	}

	/**
	 * Set read timeout on connection to given value
	 *
	 * @param timeout
	 * @return this request
	 */
	public HttpRequest readTimeout(final int timeout) {
		getConnection().setReadTimeout(timeout);
		return this;
	}

	/**
	 * Set connect timeout on connection to given value
	 *
	 * @param timeout
	 * @return this request
	 */
	public HttpRequest connectTimeout(final int timeout) {
		getConnection().setConnectTimeout(timeout);
		return this;
	}

	/**
	 * Set header name to given value
	 *
	 * @param name
	 * @param value
	 * @return this request
	 */
	public HttpRequest header(final String name, final String value) {
		getConnection().setRequestProperty(name, value);
		return this;
	}
	
	/**
	 * Set cookie. e.g: key1=val1; key2=val2;
	 * 
	 * @param name
	 * @param value
	 * @return
	 */
	public HttpRequest cookie(final String name, final String value) {
		Assert.notEmpty(name, "Cookie name must not be empty");
		Assert.notNull(value);
		
		this.cookies.put(name, value);
		this.executeCookie(getConnection());
		return this;
	}
	
	public HttpRequest cookies(final Map<String, String> cookies) {
		if(null != cookies){
			this.cookies.putAll(cookies);
			this.executeCookie(getConnection());
		}
		return this;
	}
	
	private void executeCookie(HttpURLConnection connection){
		StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> cookie : this.cookies.entrySet()) {
            if (!first)
                sb.append("; ");
            else
                first = false;
            sb.append(cookie.getKey()).append('=').append(cookie.getValue());
            // todo: spec says only ascii, no escaping / encoding defined. validate on set? or escape somehow here?
        }
        String cookies = sb.toString();
        connection.setRequestProperty(Header.HEADER_COOKIE, cookies);
	}
	
	
	/**
	 * Set header name to given value
	 *
	 * @param name
	 * @param value
	 * @return this request
	 */
	public HttpRequest header(final String name, final Number value) {
		return header(name, value != null ? value.toString() : null);
	}

	/**
	 * Set all headers found in given map where the keys are the header names
	 * and the values are the header values
	 *
	 * @param headers
	 * @return this request
	 */
	public HttpRequest headers(final Map<String, String> headers) {
		if (!headers.isEmpty())
			for (Entry<String, String> header : headers.entrySet())
				header(header);
		return this;
	}

	/**
	 * Set header to have given entry's key as the name and value as the value
	 *
	 * @param header
	 * @return this request
	 */
	public HttpRequest header(final Entry<String, String> header) {
		return header(header.getKey(), header.getValue());
	}

	/**
	 * Get a response header
	 *
	 * @param name
	 * @return response header
	 * @throws HttpRequestException
	 */
	public String header(final String name) throws HttpRequestException {
		closeOutputQuietly();
		return getConnection().getHeaderField(name);
	}

	/**
	 * Get all the response headers
	 *
	 * @return map of response header names to their value(s)
	 * @throws HttpRequestException
	 */
	public Map<String, List<String>> headers() throws HttpRequestException {
		closeOutputQuietly();
		return getConnection().getHeaderFields();
	}
	
	/**
	 * Get response cookie
	 * 	
	 * @param name	cookie name
	 * @return		cookie value
	 */
	public String cookie(final String name) {
		Assert.notEmpty(name, "Cookie name must not be empty");
		return this.cookies().get(name);
	}
	
	/**
	 * Get response cookies
	 * 	
	 * @return	cookies map
	 */
	public Map<String, String> cookies() {
		closeOutputQuietly();
		this.cookies.clear();
		Map<String, List<String>> resHeaders = getConnection().getHeaderFields();
		processResponseHeaders(resHeaders);
		return this.cookies;
	}
	
	void processResponseHeaders(Map<String, List<String>> resHeaders) {
		for (Map.Entry<String, List<String>> entry : resHeaders.entrySet()) {
            String name = entry.getKey();
            if (name == null)
                continue; // http/1.1 line
            List<String> values = entry.getValue();
            if (name.equalsIgnoreCase(Header.HEADER_SET_COOKIE)) {
                for (String value : values) {
                    if (value == null)
                        continue;
                    TokenQueue cd = new TokenQueue(value);
                    String cookieName = cd.chompTo("=").trim();
                    String cookieVal = cd.consumeTo(";").trim();
                    // ignores path, date, domain, validateTLSCertificates et al. req'd?
                    // name not blank, value not null
                    if (cookieName.length() > 0)
                        cookie(cookieName, cookieVal);
                }
            }
        }
    }
	
	/**
	 * Get a date header from the response falling back to returning -1 if the
	 * header is missing or parsing fails
	 *
	 * @param name
	 * @return date, -1 on failures
	 * @throws HttpRequestException
	 */
	public long dateHeader(final String name) throws HttpRequestException {
		return dateHeader(name, -1L);
	}

	/**
	 * Get a date header from the response falling back to returning the given
	 * default value if the header is missing or parsing fails
	 *
	 * @param name
	 * @param defaultValue
	 * @return date, default value on failures
	 * @throws HttpRequestException
	 */
	public long dateHeader(final String name, final long defaultValue) throws HttpRequestException {
		closeOutputQuietly();
		return getConnection().getHeaderFieldDate(name, defaultValue);
	}

	/**
	 * Get an integer header from the response falling back to returning -1 if
	 * the header is missing or parsing fails
	 *
	 * @param name
	 * @return header value as an integer, -1 when missing or parsing fails
	 * @throws HttpRequestException
	 */
	public int intHeader(final String name) throws HttpRequestException {
		return intHeader(name, -1);
	}

	/**
	 * Get an integer header value from the response falling back to the given
	 * default value if the header is missing or if parsing fails
	 *
	 * @param name
	 * @param defaultValue
	 * @return header value as an integer, default value when missing or parsing
	 *         fails
	 * @throws HttpRequestException
	 */
	public int intHeader(final String name, final int defaultValue) throws HttpRequestException {
		closeOutputQuietly();
		return getConnection().getHeaderFieldInt(name, defaultValue);
	}

	/**
	 * Get all values of the given header from the response
	 *
	 * @param name
	 * @return non-null but possibly empty array of {@link String} header values
	 */
	public String[] headers(final String name) {
		final Map<String, List<String>> headers = headers();
		if (headers == null || headers.isEmpty())
			return EMPTY_STRINGS;

		final List<String> values = headers.get(name);
		if (values != null && !values.isEmpty())
			return values.toArray(new String[values.size()]);
		else
			return EMPTY_STRINGS;
	}

	/**
	 * Get parameter with given name from header value in response
	 *
	 * @param headerName
	 * @param paramName
	 * @return parameter value or null if missing
	 */
	public String parameter(final String headerName, final String paramName) {
		return getParam(header(headerName), paramName);
	}

	/**
	 * Get all parameters from header value in response
	 * <p>
	 * This will be all key=value pairs after the first ';' that are separated
	 * by a ';'
	 *
	 * @param headerName
	 * @return non-null but possibly empty map of parameter headers
	 */
	public Map<String, String> parameters(final String headerName) {
		return getParams(header(headerName));
	}

	/**
	 * Get parameter values from header value
	 *
	 * @param header
	 * @return parameter value or null if none
	 */
	protected Map<String, String> getParams(final String header) {
		if (header == null || header.length() == 0)
			return Collections.emptyMap();

		final int headerLength = header.length();
		int start = header.indexOf(';') + 1;
		if (start == 0 || start == headerLength)
			return Collections.emptyMap();

		int end = header.indexOf(';', start);
		if (end == -1)
			end = headerLength;

		Map<String, String> params = new LinkedHashMap<String, String>();
		while (start < end) {
			int nameEnd = header.indexOf('=', start);
			if (nameEnd != -1 && nameEnd < end) {
				String name = header.substring(start, nameEnd).trim();
				if (name.length() > 0) {
					String value = header.substring(nameEnd + 1, end).trim();
					int length = value.length();
					if (length != 0)
						if (length > 2 && '"' == value.charAt(0) && '"' == value.charAt(length - 1))
							params.put(name, value.substring(1, length - 1));
						else
							params.put(name, value);
				}
			}

			start = end + 1;
			end = header.indexOf(';', start);
			if (end == -1)
				end = headerLength;
		}

		return params;
	}

	/**
	 * Get parameter value from header value
	 *
	 * @param value
	 * @param paramName
	 * @return parameter value or null if none
	 */
	protected String getParam(final String value, final String paramName) {
		if (value == null || value.length() == 0)
			return null;

		final int length = value.length();
		int start = value.indexOf(';') + 1;
		if (start == 0 || start == length)
			return null;

		int end = value.indexOf(';', start);
		if (end == -1)
			end = length;

		while (start < end) {
			int nameEnd = value.indexOf('=', start);
			if (nameEnd != -1 && nameEnd < end && paramName.equals(value.substring(start, nameEnd).trim())) {
				String paramValue = value.substring(nameEnd + 1, end).trim();
				int valueLength = paramValue.length();
				if (valueLength != 0)
					if (valueLength > 2 && '"' == paramValue.charAt(0) && '"' == paramValue.charAt(valueLength - 1))
						return paramValue.substring(1, valueLength - 1);
					else
						return paramValue;
			}

			start = end + 1;
			end = value.indexOf(';', start);
			if (end == -1)
				end = length;
		}

		return null;
	}

	/**
	 * Get 'charset' parameter from 'Content-Type' response header
	 *
	 * @return charset or null if none
	 */
	public String charset() {
		return parameter(Header.HEADER_CONTENT_TYPE, Header.PARAM_CHARSET);
	}

	/**
	 * Set the 'User-Agent' header to given value
	 *
	 * @param userAgent
	 * @return this request
	 */
	public HttpRequest userAgent(final String userAgent) {
		return header(Header.HEADER_USER_AGENT, userAgent);
	}

	/**
	 * Set the 'Referer' header to given value
	 *
	 * @param referer
	 * @return this request
	 */
	public HttpRequest referer(final String referer) {
		return header(Header.HEADER_REFERER, referer);
	}

	/**
	 * Set value of {@link HttpURLConnection#setUseCaches(boolean)}
	 *
	 * @param useCaches
	 * @return this request
	 */
	public HttpRequest useCaches(final boolean useCaches) {
		getConnection().setUseCaches(useCaches);
		return this;
	}

	/**
	 * Set the 'Accept-Encoding' header to given value
	 *
	 * @param acceptEncoding
	 * @return this request
	 */
	public HttpRequest acceptEncoding(final String acceptEncoding) {
		return header(Header.HEADER_ACCEPT_ENCODING, acceptEncoding);
	}

	/**
	 * Set the 'Accept-Encoding' header to 'gzip'
	 *
	 * @see #uncompress(boolean)
	 * @return this request
	 */
	public HttpRequest acceptGzipEncoding() {
		return acceptEncoding(Header.ENCODING_GZIP);
	}

	/**
	 * Set the 'Accept-Charset' header to given value
	 *
	 * @param acceptCharset
	 * @return this request
	 */
	public HttpRequest acceptCharset(final String acceptCharset) {
		return header(Header.HEADER_ACCEPT_CHARSET, acceptCharset);
	}

	/**
	 * Get the 'Content-Encoding' header from the response
	 *
	 * @return this request
	 */
	public String contentEncoding() {
		return header(Header.HEADER_CONTENT_ENCODING);
	}

	/**
	 * Get the 'Server' header from the response
	 *
	 * @return server
	 */
	public String server() {
		return header(Header.HEADER_SERVER);
	}

	/**
	 * Get the 'Date' header from the response
	 *
	 * @return date value, -1 on failures
	 */
	public long date() {
		return dateHeader(Header.HEADER_DATE);
	}

	/**
	 * Get the 'Cache-Control' header from the response
	 *
	 * @return cache control
	 */
	public String cacheControl() {
		return header(Header.HEADER_CACHE_CONTROL);
	}

	/**
	 * Get the 'ETag' header from the response
	 *
	 * @return entity tag
	 */
	public String eTag() {
		return header(Header.HEADER_ETAG);
	}

	/**
	 * Get the 'Expires' header from the response
	 *
	 * @return expires value, -1 on failures
	 */
	public long expires() {
		return dateHeader(Header.HEADER_EXPIRES);
	}

	/**
	 * Get the 'Last-Modified' header from the response
	 *
	 * @return last modified value, -1 on failures
	 */
	public long lastModified() {
		return dateHeader(Header.HEADER_LAST_MODIFIED);
	}

	/**
	 * Get the 'Location' header from the response
	 *
	 * @return location
	 */
	public String location() {
		return header(Header.HEADER_LOCATION);
	}

	/**
	 * Set the 'Authorization' header to given value
	 *
	 * @param authorization
	 * @return this request
	 */
	public HttpRequest authorization(final String authorization) {
		return header(Header.HEADER_AUTHORIZATION, authorization);
	}

	/**
	 * Set the 'Proxy-Authorization' header to given value
	 *
	 * @param proxyAuthorization
	 * @return this request
	 */
	public HttpRequest proxyAuthorization(final String proxyAuthorization) {
		return header(Header.HEADER_PROXY_AUTHORIZATION, proxyAuthorization);
	}

	/**
	 * Set the 'Authorization' header to given values in Basic authentication
	 * format
	 *
	 * @param name
	 * @param password
	 * @return this request
	 */
	public HttpRequest basic(final String name, final String password) {
		return authorization("Basic " + new String(EncodeKit.base64Encode(name + ':' + password)));
	}

	/**
	 * Set the 'Proxy-Authorization' header to given values in Basic
	 * authentication format
	 *
	 * @param name
	 * @param password
	 * @return this request
	 */
	public HttpRequest proxyBasic(final String name, final String password) {
		return proxyAuthorization("Basic " + new String(EncodeKit.base64Encode(name + ':' + password)));
	}

	/**
	 * Set the 'If-Modified-Since' request header to the given value
	 *
	 * @param ifModifiedSince
	 * @return this request
	 */
	public HttpRequest ifModifiedSince(final long ifModifiedSince) {
		getConnection().setIfModifiedSince(ifModifiedSince);
		return this;
	}

	/**
	 * Set the 'If-None-Match' request header to the given value
	 *
	 * @param ifNoneMatch
	 * @return this request
	 */
	public HttpRequest ifNoneMatch(final String ifNoneMatch) {
		return header(Header.HEADER_IF_NONE_MATCH, ifNoneMatch);
	}

	/**
	 * Set the 'Content-Type' request header to the given value
	 *
	 * @param contentType
	 * @return this request
	 */
	public HttpRequest contentType(final String contentType) {
		return contentType(contentType, null);
	}

	/**
	 * Set the 'Content-Type' request header to the given value and charset
	 *
	 * @param contentType
	 * @param charset
	 * @return this request
	 */
	public HttpRequest contentType(final String contentType, final String charset) {
		if (charset != null && charset.length() > 0) {
			final String separator = "; " + Header.PARAM_CHARSET + '=';
			return header(Header.HEADER_CONTENT_TYPE, contentType + separator + charset);
		} else
			return header(Header.HEADER_CONTENT_TYPE, contentType);
	}

	/**
	 * Get the 'Content-Type' header from the response
	 *
	 * @return response header value
	 */
	public String contentType() {
		return header(Header.HEADER_CONTENT_TYPE);
	}

	/**
	 * Get the 'Content-Length' header from the response
	 *
	 * @return response header value
	 */
	public int contentLength() {
		return intHeader(Header.HEADER_CONTENT_LENGTH);
	}

	/**
	 * Set the 'Content-Length' request header to the given value
	 *
	 * @param contentLength
	 * @return this request
	 */
	public HttpRequest contentLength(final String contentLength) {
		return contentLength(Integer.parseInt(contentLength));
	}

	/**
	 * Set the 'Content-Length' request header to the given value
	 *
	 * @param contentLength
	 * @return this request
	 */
	public HttpRequest contentLength(final int contentLength) {
		getConnection().setFixedLengthStreamingMode(contentLength);
		return this;
	}

	/**
	 * Set the 'Accept' header to given value
	 *
	 * @param accept
	 * @return this request
	 */
	public HttpRequest accept(final String accept) {
		return header(Header.HEADER_ACCEPT, accept);
	}

	/**
	 * Set the 'Accept' header to 'application/json'
	 *
	 * @return this request
	 */
	public HttpRequest acceptJson() {
		return accept(Header.CONTENT_TYPE_JSON);
	}

	/**
	 * Copy from input stream to output stream
	 *
	 * @param input
	 * @param output
	 * @return this request
	 * @throws IOException
	 */
	protected HttpRequest copy(final InputStream input, final OutputStream output) throws IOException {
		return new CloseOperation<HttpRequest>(input, ignoreCloseExceptions) {

			@Override
			public HttpRequest run() throws IOException {
				final byte[] buffer = new byte[bufferSize];
				int read;
				while ((read = input.read(buffer)) != -1) {
					output.write(buffer, 0, read);
					totalWritten += read;
					progress.onUpload(totalWritten, totalSize);
				}
				return HttpRequest.this;
			}
		}.call();
	}

	/**
	 * Copy from reader to writer
	 *
	 * @param input
	 * @param output
	 * @return this request
	 * @throws IOException
	 */
	protected HttpRequest copy(final Reader input, final Writer output) throws IOException {
		return new CloseOperation<HttpRequest>(input, ignoreCloseExceptions) {

			@Override
			public HttpRequest run() throws IOException {
				final char[] buffer = new char[bufferSize];
				int read;
				while ((read = input.read(buffer)) != -1) {
					output.write(buffer, 0, read);
					totalWritten += read;
					progress.onUpload(totalWritten, -1);
				}
				return HttpRequest.this;
			}
		}.call();
	}

	/**
	 * Set the UploadProgress callback for this request
	 *
	 * @param callback
	 * @return this request
	 */
	public HttpRequest progress(final UploadProgress callback) {
		if (callback == null)
			progress = UploadProgress.DEFAULT;
		else
			progress = callback;
		return this;
	}

	private HttpRequest incrementTotalSize(final long size) {
		if (totalSize == -1)
			totalSize = 0;
		totalSize += size;
		return this;
	}

	/**
	 * Close output stream
	 *
	 * @return this request
	 * @throws HttpRequestException
	 * @throws IOException
	 */
	protected HttpRequest closeOutput() throws IOException {
		progress(null);
		if (output == null)
			return this;
		if (multipart)
			output.write(CRLF + "--" + BOUNDARY + "--" + CRLF);
		if (ignoreCloseExceptions)
			try {
				output.close();
			} catch (IOException ignored) {
				// Ignored
			}
		else
			output.close();
		output = null;
		return this;
	}

	/**
	 * Call {@link #closeOutput()} and re-throw a caught {@link IOException}s as
	 * an {@link HttpRequestException}
	 *
	 * @return this request
	 * @throws HttpRequestException
	 */
	protected HttpRequest closeOutputQuietly() throws HttpRequestException {
		try {
			return closeOutput();
		} catch (IOException e) {
			throw new HttpRequestException(e);
		}
	}

	/**
	 * Open output stream
	 *
	 * @return this request
	 * @throws IOException
	 */
	protected HttpRequest openOutput() throws IOException {
		if (output != null)
			return this;
		getConnection().setDoOutput(true);
		final String charset = getParam(getConnection().getRequestProperty(Header.HEADER_CONTENT_TYPE),
				Header.PARAM_CHARSET);
		output = new RequestOutputStream(getConnection().getOutputStream(), charset, bufferSize);
		return this;
	}

	/**
	 * Start part of a multipart
	 *
	 * @return this request
	 * @throws IOException
	 */
	protected HttpRequest startPart() throws IOException {
		if (!multipart) {
			multipart = true;
			contentType(CONTENT_TYPE_MULTIPART).openOutput();
			output.write("--" + BOUNDARY + CRLF);
		} else
			output.write(CRLF + "--" + BOUNDARY + CRLF);
		return this;
	}

	/**
	 * Write part header
	 *
	 * @param name
	 * @param filename
	 * @return this request
	 * @throws IOException
	 */
	protected HttpRequest writePartHeader(final String name, final String filename) throws IOException {
		return writePartHeader(name, filename, null);
	}

	/**
	 * Write part header
	 *
	 * @param name
	 * @param filename
	 * @param contentType
	 * @return this request
	 * @throws IOException
	 */
	protected HttpRequest writePartHeader(final String name, final String filename, final String contentType)
			throws IOException {
		final StringBuilder partBuffer = new StringBuilder();
		partBuffer.append("form-data; name=\"").append(name);
		if (filename != null)
			partBuffer.append("\"; filename=\"").append(filename);
		partBuffer.append('"');
		partHeader("Content-Disposition", partBuffer.toString());
		if (contentType != null)
			partHeader(Header.HEADER_CONTENT_TYPE, contentType);
		return send(CRLF);
	}

	/**
	 * Write part of a multipart request to the request body
	 *
	 * @param name
	 * @param part
	 * @return this request
	 */
	public HttpRequest part(final String name, final String part) {
		return part(name, null, part);
	}

	/**
	 * Write part of a multipart request to the request body
	 *
	 * @param name
	 * @param filename
	 * @param part
	 * @return this request
	 * @throws HttpRequestException
	 */
	public HttpRequest part(final String name, final String filename, final String part) throws HttpRequestException {
		return part(name, filename, null, part);
	}

	/**
	 * Write part of a multipart request to the request body
	 *
	 * @param name
	 * @param filename
	 * @param contentType
	 *            value of the Content-Type part header
	 * @param part
	 * @return this request
	 * @throws HttpRequestException
	 */
	public HttpRequest part(final String name, final String filename, final String contentType, final String part)
			throws HttpRequestException {
		try {
			startPart();
			writePartHeader(name, filename, contentType);
			output.write(part);
		} catch (IOException e) {
			throw new HttpRequestException(e);
		}
		return this;
	}

	/**
	 * Write part of a multipart request to the request body
	 *
	 * @param name
	 * @param part
	 * @return this request
	 * @throws HttpRequestException
	 */
	public HttpRequest part(final String name, final Number part) throws HttpRequestException {
		return part(name, null, part);
	}

	/**
	 * Write part of a multipart request to the request body
	 *
	 * @param name
	 * @param filename
	 * @param part
	 * @return this request
	 * @throws HttpRequestException
	 */
	public HttpRequest part(final String name, final String filename, final Number part) throws HttpRequestException {
		return part(name, filename, part != null ? part.toString() : null);
	}

	/**
	 * Write part of a multipart request to the request body
	 *
	 * @param name
	 * @param part
	 * @return this request
	 * @throws HttpRequestException
	 */
	public HttpRequest part(final String name, final File part) throws HttpRequestException {
		return part(name, null, part);
	}

	/**
	 * Write part of a multipart request to the request body
	 *
	 * @param name
	 * @param filename
	 * @param part
	 * @return this request
	 * @throws HttpRequestException
	 */
	public HttpRequest part(final String name, final String filename, final File part) throws HttpRequestException {
		return part(name, filename, null, part);
	}

	/**
	 * Write part of a multipart request to the request body
	 *
	 * @param name
	 * @param filename
	 * @param contentType
	 *            value of the Content-Type part header
	 * @param part
	 * @return this request
	 * @throws HttpRequestException
	 */
	public HttpRequest part(final String name, final String filename, final String contentType, final File part)
			throws HttpRequestException {
		final InputStream stream;
		try {
			stream = new BufferedInputStream(new FileInputStream(part));
			incrementTotalSize(part.length());
		} catch (IOException e) {
			throw new HttpRequestException(e);
		}
		return part(name, filename, contentType, stream);
	}

	/**
	 * Write part of a multipart request to the request body
	 *
	 * @param name
	 * @param part
	 * @return this request
	 * @throws HttpRequestException
	 */
	public HttpRequest part(final String name, final InputStream part) throws HttpRequestException {
		return part(name, null, null, part);
	}

	/**
	 * Write part of a multipart request to the request body
	 *
	 * @param name
	 * @param filename
	 * @param contentType
	 *            value of the Content-Type part header
	 * @param part
	 * @return this request
	 * @throws HttpRequestException
	 */
	public HttpRequest part(final String name, final String filename, final String contentType, final InputStream part)
			throws HttpRequestException {
		try {
			startPart();
			writePartHeader(name, filename, contentType);
			copy(part, output);
		} catch (IOException e) {
			throw new HttpRequestException(e);
		}
		return this;
	}

	/**
	 * Write a multipart header to the response body
	 *
	 * @param name
	 * @param value
	 * @return this request
	 * @throws HttpRequestException
	 */
	public HttpRequest partHeader(final String name, final String value) throws HttpRequestException {
		return send(name).send(": ").send(value).send(CRLF);
	}

	/**
	 * Write contents of file to request body
	 *
	 * @param input
	 * @return this request
	 * @throws HttpRequestException
	 */
	public HttpRequest send(final File input) throws HttpRequestException {
		final InputStream stream;
		try {
			stream = new BufferedInputStream(new FileInputStream(input));
			incrementTotalSize(input.length());
		} catch (FileNotFoundException e) {
			throw new HttpRequestException(e);
		}
		return send(stream);
	}

	/**
	 * Write byte array to request body
	 *
	 * @param input
	 * @return this request
	 * @throws HttpRequestException
	 */
	public HttpRequest send(final byte[] input) throws HttpRequestException {
		if (input != null)
			incrementTotalSize(input.length);
		return send(new ByteArrayInputStream(input));
	}

	/**
	 * Write stream to request body
	 * <p>
	 * The given stream will be closed once sending completes
	 *
	 * @param input
	 * @return this request
	 * @throws HttpRequestException
	 */
	public HttpRequest send(final InputStream input) throws HttpRequestException {
		try {
			openOutput();
			copy(input, output);
		} catch (IOException e) {
			throw new HttpRequestException(e);
		}
		return this;
	}

	/**
	 * Write reader to request body
	 * <p>
	 * The given reader will be closed once sending completes
	 *
	 * @param input
	 * @return this request
	 * @throws HttpRequestException
	 */
	public HttpRequest send(final Reader input) throws HttpRequestException {
		try {
			openOutput();
		} catch (IOException e) {
			throw new HttpRequestException(e);
		}
		final Writer writer = new OutputStreamWriter(output, output.encoder.charset());
		return new FlushOperation<HttpRequest>(writer) {

			@Override
			protected HttpRequest run() throws IOException {
				return copy(input, writer);
			}
		}.call();
	}

	/**
	 * Write char sequence to request body
	 * <p>
	 * The charset configured via {@link #contentType(String)} will be used and
	 * UTF-8 will be used if it is unset.
	 *
	 * @param value
	 * @return this request
	 * @throws HttpRequestException
	 */
	public HttpRequest send(final String value) throws HttpRequestException {
		try {
			openOutput();
			output.write(value.toString());
		} catch (IOException e) {
			throw new HttpRequestException(e);
		}
		return this;
	}

	/**
	 * Create writer to request output stream
	 *
	 * @return writer
	 * @throws HttpRequestException
	 */
	public OutputStreamWriter writer() throws HttpRequestException {
		try {
			openOutput();
			return new OutputStreamWriter(output, output.encoder.charset());
		} catch (IOException e) {
			throw new HttpRequestException(e);
		}
	}

	/**
	 * Write the values in the map as form data to the request body
	 * <p>
	 * The pairs specified will be URL-encoded in UTF-8 and sent with the
	 * 'application/x-www-form-urlencoded' content-type
	 *
	 * @param values
	 * @return this request
	 * @throws HttpRequestException
	 */
	public HttpRequest form(final Map<?, ?> values) throws HttpRequestException {
		return form(values, Header.CHARSET_UTF8);
	}

	/**
	 * Write the key and value in the entry as form data to the request body
	 * <p>
	 * The pair specified will be URL-encoded in UTF-8 and sent with the
	 * 'application/x-www-form-urlencoded' content-type
	 *
	 * @param entry
	 * @return this request
	 * @throws HttpRequestException
	 */
	public HttpRequest form(final Entry<?, ?> entry) throws HttpRequestException {
		return form(entry, Header.CHARSET_UTF8);
	}

	/**
	 * Write the key and value in the entry as form data to the request body
	 * <p>
	 * The pair specified will be URL-encoded and sent with the
	 * 'application/x-www-form-urlencoded' content-type
	 *
	 * @param entry
	 * @param charset
	 * @return this request
	 * @throws HttpRequestException
	 */
	public HttpRequest form(final Entry<?, ?> entry, final String charset) throws HttpRequestException {
		return form(entry.getKey(), entry.getValue(), charset);
	}

	/**
	 * Write the name/value pair as form data to the request body
	 * <p>
	 * The pair specified will be URL-encoded in UTF-8 and sent with the
	 * 'application/x-www-form-urlencoded' content-type
	 *
	 * @param name
	 * @param value
	 * @return this request
	 * @throws HttpRequestException
	 */
	public HttpRequest form(final Object name, final Object value) throws HttpRequestException {
		return form(name, value, Header.CHARSET_UTF8);
	}

	/**
	 * Write the name/value pair as form data to the request body
	 * <p>
	 * The values specified will be URL-encoded and sent with the
	 * 'application/x-www-form-urlencoded' content-type
	 *
	 * @param name
	 * @param value
	 * @param charset
	 * @return this request
	 * @throws HttpRequestException
	 */
	public HttpRequest form(final Object name, final Object value, String charset) throws HttpRequestException {
		final boolean first = !form;
		if (first) {
			contentType(Header.CONTENT_TYPE_FORM, charset);
			form = true;
		}
		charset = getValidCharset(charset);
		try {
			openOutput();
			if (!first)
				output.write('&');
			output.write(URLEncoder.encode(name.toString(), charset));
			output.write('=');
			if (value != null)
				output.write(URLEncoder.encode(value.toString(), charset));
		} catch (IOException e) {
			throw new HttpRequestException(e);
		}
		return this;
	}

	/**
	 * Write the values in the map as encoded form data to the request body
	 *
	 * @param values
	 * @param charset
	 * @return this request
	 * @throws HttpRequestException
	 */
	public HttpRequest form(final Map<?, ?> values, final String charset) throws HttpRequestException {
		if (!values.isEmpty())
			for (Entry<?, ?> entry : values.entrySet())
				form(entry, charset);
		return this;
	}

	/**
	 * Configure HTTPS connection to trust all certificates
	 * <p>
	 * This method does nothing if the current request is not a HTTPS request
	 *
	 * @return this request
	 * @throws HttpRequestException
	 */
	public HttpRequest trustAllCerts() throws HttpRequestException {
		final HttpURLConnection connection = getConnection();
		if (connection instanceof HttpsURLConnection)
			((HttpsURLConnection) connection).setSSLSocketFactory(getTrustedFactory());
		return this;
	}

	/**
	 * Configure HTTPS connection to trust all hosts using a custom
	 * {@link HostnameVerifier} that always returns <code>true</code> for each
	 * host verified
	 * <p>
	 * This method does nothing if the current request is not a HTTPS request
	 *
	 * @return this request
	 */
	public HttpRequest trustAllHosts() {
		final HttpURLConnection connection = getConnection();
		if (connection instanceof HttpsURLConnection)
			((HttpsURLConnection) connection).setHostnameVerifier(getTrustedVerifier());
		return this;
	}

	/**
	 * Get the {@link URL} of this request's connection
	 *
	 * @return request URL
	 */
	public URL url() {
		return getConnection().getURL();
	}

	/**
	 * Get the HTTP method of this request
	 *
	 * @return method
	 */
	public String method() {
		return getConnection().getRequestMethod();
	}

	/**
	 * Configure an HTTP proxy on this connection. Use {
	 * {@link #proxyBasic(String, String)} if this proxy requires basic
	 * authentication.
	 *
	 * @param proxyHost
	 * @param proxyPort
	 * @return this request
	 */
	public HttpRequest useProxy(final String proxyHost, final int proxyPort) {
		if (connection != null)
			throw new IllegalStateException(
					"The connection has already been created. This method must be called before reading or writing to the request.");

		this.httpProxyHost = proxyHost;
		this.httpProxyPort = proxyPort;
		return this;
	}

	/**
	 * Set whether or not the underlying connection should follow redirects in
	 * the response.
	 *
	 * @param followRedirects
	 *            - true fo follow redirects, false to not.
	 * @return this request
	 */
	public HttpRequest followRedirects(final boolean followRedirects) {
		getConnection().setInstanceFollowRedirects(followRedirects);
		return this;
	}
}