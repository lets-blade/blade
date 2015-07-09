
package blade.kit.http;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import blade.kit.io.StringBuilderWriter;

/**
 * http基类
 * <p>
 * </p>
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 * @param <T>
 */
@SuppressWarnings("unchecked")
public abstract class HttpBase<T> {

	//////////////////////////const///////////////////////////////////
	
	/**Accept*/
	public static final String HEADER_ACCEPT = "Accept";
	
	/**Accept-Encoding*/
	public static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
	
	/**User-Agent*/
	public static final String HEADER_USER_AGENT = "User-Agent";
	
	/**Content-Type*/
	public static final String HEADER_CONTENT_TYPE = "Content-Type";
	
	/**Content-Length*/
	public static final String HEADER_CONTENT_LENGTH = "Content-Length";
	
	/**Content-Encoding*/
	public static final String HEADER_CONTENT_ENCODING = "Content-Encoding";
	
	/**Host*/
	public static final String HEADER_HOST = "Host";
	
	/**ETag*/
	public static final String HEADER_ETAG = "ETag";
	
	/**Connection*/
	public static final String HEADER_CONNECTION = "Connection";
	
	/**Keep-Alive*/
	public static final String HEADER_KEEP_ALIVE = "Keep-Alive";
	
	/**Close*/
	public static final String HEADER_CLOSE = "Close";
	
	/**HTTP/1.0*/
	public static final String HTTP_1_0 = "HTTP/1.0";
	
	/**HTTP/1.1*/
	public static final String HTTP_1_1 = "HTTP/1.1";
	
	/**UTF-8*/
	protected String charset = "UTF-8";
	
	/**默认缓冲*/
	protected static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

	/**http版本*/
	protected String httpVersion = HTTP_1_1;
	
	/**存储头信息*/
	protected Map<String, List<String>> headers = new HashMap<String, List<String>>();
	
	/**存储表单数据*/
	protected Map<String, Object> form = new HashMap<String, Object>();
	
	/**存储响应主体*/
	protected String body;
	
	/**
	 * 返回http版本
	 * @return String
	 */
	public String httpVersion() {
		return httpVersion;
	}

	/**
	 * 设置http版本
	 * @param httpVersion
	 * @return T
	 */
	public T httpVersion(String httpVersion) {
		this.httpVersion = httpVersion;
		return (T) this;
	}
	
	// ---------------------------------------------------------------- headers

	/**
	 * 根据name获取头信息
	 * @param name
	 * @return String
	 */
	public String header(String name) {
		String key = name.trim();
		if(null == headers.get(key)){
			return null;
		}
		String value = headers.get(key).get(0);
		if (value == null) {
			return null;
		}
		return value;
	}
	
	/**
	 * 移除一个头信息
	 * @param name
	 */
	public void removeHeader(String name) {
		String key = name.trim();
		headers.remove(key);
	}

	/**
	 * 设置一个header
	 * @param name
	 * @param value
	 * @return T
	 */
	public T header(String name, String value) {
		if(null != name && null != value){
			String key = name.trim();
			value = value.trim();
			headers.put(key, Arrays.asList(value));
		}
		return (T) this;
	}
	
	/**
	 * 覆盖一个header
	 * @param name
	 * @param value
	 * @param flag
	 * @return T
	 */
	public T header(String name, String value, boolean flag) {
		if(null != name && null != value){
			String key = name.trim();
			value = value.trim();
			if(headers.containsKey(key)){
				headers.remove(key);
			}
			headers.put(key, Arrays.asList(value));
		}
		return (T) this;
	}

	/**
	 * header
	 * @param name
	 * @param value
	 * @return T
	 */
	public T header(String name, int value) {
		return header(name, String.valueOf(value));
	}

	/**
	 * header
	 * @param name
	 * @param millis
	 * @return T
	 */
	public T header(String name, long millis) {
		return header(name, String.valueOf(millis));
	}

	/**
	 * 获取headers
	 * @return Map<String, List<String>>
	 */
	public Map<String, List<String>> headers() {
		return Collections.unmodifiableMap(headers);
	}

	/**
	 * 返回字符集
	 * @return String
	 */
	public String charset() {
		return charset;
	}

	/**
	 * 设置字符集
	 * @param charset
	 * @return T
	 */
	public T charset(String charset) {
		this.charset = null;
		contentType(null, charset);
		return (T) this;
	}

	/**mediaType*/
	protected String mediaType;

	/**
	 * 获取mediaType
	 * @return String
	 */
	public String mediaType() {
		return mediaType;
	}

	/**
	 * 设置mediaType
	 * @param mediaType
	 * @return T
	 */
	public T mediaType(String mediaType) {
		contentType(mediaType, null);
		return (T) this;
	}

	/**
	 * 获取contentType
	 * @return String
	 */
	public String contentType() {
		return header(HEADER_CONTENT_TYPE);
	}

	/**
	 * 设置contentType
	 * @param contentType
	 * @return T
	 */
	public T contentType(String contentType) {
		header(HEADER_CONTENT_TYPE, contentType);
		return (T) this;
	}

	/**
	 * 设置mediaType包含字符集
	 * @param mediaType
	 * @param charset
	 * @return T
	 */
	public T contentType(String mediaType, String charset) {
		if (mediaType == null) {
			mediaType = this.mediaType;
		} else {
			this.mediaType = mediaType;
		}

		if (charset == null) {
			charset = this.charset;
		} else {
			this.charset = charset;
		}

		String contentType = mediaType;
		if (charset != null) {
			contentType += ";charset=" + charset;
		}

		header(HEADER_CONTENT_TYPE, contentType);
		return (T) this;
	}
	
	/**
	 * 设置是否为活动连接
	 * @param keepAlive
	 * @return T
	 */
	public T connectionKeepAlive(boolean keepAlive) {
		if (keepAlive) {
			header(HEADER_CONNECTION, HEADER_KEEP_ALIVE);
		} else {
			header(HEADER_CONNECTION, HEADER_CLOSE);
		}
		return (T) this;
	}

	/**
	 * 获取是否为活动连接
	 * @return boolean
	 */
	public boolean isConnectionPersistent() {
		String connection = header(HEADER_CONNECTION);
		if (connection == null) {
			return !httpVersion.equalsIgnoreCase(HTTP_1_0);
		}

		return !connection.equalsIgnoreCase(HEADER_CLOSE);
	}

	/**
	 * 获取内容长度
	 * @return String
	 */
	public String contentLength() {
		return header(HEADER_CONTENT_LENGTH);
	}

	/**
	 * 设置内容长度
	 * @param value
	 * @return T
	 */
	public T contentLength(int value) {
		header(HEADER_CONTENT_LENGTH, String.valueOf(value));
		return (T) this;
	}

	/**
	 * 获取内容编码
	 * @return String
	 */
	public String contentEncoding() {
		return header(HEADER_CONTENT_ENCODING);
	}

	/**
	 * 获取请求头
	 * @return String
	 */
	public String accept() {
		return header(HEADER_ACCEPT);
	}
	
	/**
	 * 设置请求头
	 * @param encodings
	 * @return T
	 */
	public T accept(String encodings) {
		header(HEADER_ACCEPT, encodings);
		return (T) this;
	}
	
	/**
	 * 获取请求编码
	 * @return String
	 */
	public String acceptEncoding() {
		return header(HEADER_ACCEPT_ENCODING);
	}

	/**
	 * 设置请求编码
	 * @param encodings
	 * @return T
	 */
	public T acceptEncoding(String encodings) {
		header(HEADER_ACCEPT_ENCODING, encodings);
		return (T) this;
	}

	/**
	 * 初始化表单
	 */
	protected void initForm() {
		if (form == null) {
			form = new HashMap<String, Object>();
		}
	}

	/**
	 * 转换数据类型
	 * @param value
	 * @return Object
	 */
	protected Object wrapFormValue(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof CharSequence) {
			return value.toString();
		}
		if (value instanceof Number) {
			return value.toString();
		}
		if (value instanceof Boolean) {
			return value.toString();
		}
		throw new HttpException("Unsupported value type: " + value.getClass().getName());
	}

	/**
	 * 设置表单数据
	 * @param name
	 * @param value
	 * @return T
	 */
	public T query(String name, Object value) {
		initForm();
		value = wrapFormValue(value);
		form.put(name, value);

		return (T) this;
	}

	/**
	 * 设置表单数据，是否覆盖
	 * @param name
	 * @param value
	 * @param overwrite
	 * @return T
	 */
	public T query(String name, Object value, boolean overwrite) {
		initForm();

		value = wrapFormValue(value);

		if (overwrite) {
			form.put(name, value);
		} else {
			form.put(name, value);
		}

		return (T) this;
	}

	/**
	 * 设置表单数据
	 * @param name
	 * @param value
	 * @param parameters
	 * @return T
	 */
	public T query(String name, Object value, Object... parameters) {
		initForm();

		query(name, value);

		for (int i = 0; i < parameters.length; i += 2) {
			name = parameters[i].toString();

			query(name, parameters[i + 1]);
		}
		return (T) this;
	}

	/**
	 * 设置map类型表单数据
	 * @param formMap
	 * @return T
	 */
	public T query(Map<String, Object> formMap) {
		initForm();

		for (Map.Entry<String, Object> entry : formMap.entrySet()) {
			query(entry.getKey(), entry.getValue());
		}
		return (T) this;
	}

	/**
	 * 获取表单数据
	 * @return Map<String, Object>
	 */
	public Map<String, Object> form() {
		return form;
	}

	// ---------------------------------------------------------------- form encoding

	/**formEncoding*/
	protected String formEncoding = "UTF-8";
	
	/**
	 * 设置表单编码
	 * @param encoding
	 * @return T
	 */
	public T formEncoding(String encoding) {
		this.formEncoding = encoding;
		return (T) this;
	}

	/**
	 * 获取响应主体
	 * @return String
	 */
	public String body() {
		return body;
	}

	/**
	 * 获取响应流字节码
	 * @return byte[]
	 */
	public byte[] bodyBytes() {
		if (body == null) {
			return null;
		}
		try {
			return body.getBytes("ISO8859-1");
		} catch (UnsupportedEncodingException ignore) {
			return null;
		}
	}

	/**
	 * 获取响应文本
	 * @return String
	 */
	public String bodyText() {
		if (body == null) {
			return null;
		}
		if (charset != null) {
			try {
				return new String(body.getBytes("ISO8859-1"), charset);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return body();
	}

	/**
	 * 设置内容主体
	 * @param body
	 * @return T
	 */
	public T body(String body) {
		this.body = body;
		this.form = null;
		contentLength(body.length());
		return (T) this;
	}

	/**
	 * 设置内容主体编码
	 * @param charset
	 * @return String
	 */
	public String bodyText(String charset) {
		try {
			return new String(body.getBytes(charset), "ISO8859-1");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 设置内容mediaType
	 * @param body
	 * @param mediaType
	 * @return T
	 */
	public T bodyText(String body, String mediaType) {
		return bodyText(body, mediaType, "UTF-8");
	}
	
	/**
	 * 设置内容html
	 * @param body
	 * @return T
	 */
	public T bodyHtml(String body) {
		return bodyText(body, "text/html", "UTF-8");
	}
	
	/**
	 * 设置主体文本
	 * @param body
	 * @param mediaType
	 * @param charset
	 * @return T
	 */
	public T bodyText(String body, String mediaType, String charset) {
		try {
			body = new String(body.getBytes(charset), "ISO8859-1");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		contentType(mediaType, charset);
		body(body);
		return (T) this;
	}

	/**
	 * 设置主体字节码
	 * @param content
	 * @param contentType
	 * @return T
	 */
	public T body(byte[] content, String contentType) {
		String body = null;
		try {
			body = new String(content, "ISO8859-1");
		} catch (UnsupportedEncodingException ignore) {
		}
		contentType(contentType);
		return body(body);
	}
	
	/**
	 * 读取主体
	 * @param input
	 * @return String
	 * @throws IOException
	 */
	public String readBody(InputStream input) throws IOException{
		StringBuilderWriter sw = new StringBuilderWriter();
        InputStreamReader in = new InputStreamReader(input, Charset.defaultCharset());
        copy(in, sw);
        close(input);
        return sw.toString();
	}
	
	/**
	 * 复制数据流
	 * @param input
	 * @param output
	 * @return int
	 * @throws IOException
	 */
	public int copy(Reader input, Writer output) throws IOException {
		long count = copyLarge(input, output);
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) count;
	}
	
	/**
	 * 复制长数据流
	 * @param input
	 * @param output
	 * @return long
	 * @throws IOException
	 */
	public long copyLarge(Reader input, Writer output) throws IOException {
        return copyLarge(input, output, new char[DEFAULT_BUFFER_SIZE]);
    }
	
	/**
	 * 复制长数据流
	 * @param input
	 * @param output
	 * @param buffer
	 * @return long
	 * @throws IOException
	 */
	public long copyLarge(Reader input, Writer output, char [] buffer) throws IOException {
        long count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }
	
	/**
	 * 关闭数据流
	 * @param closeable
	 * @throws IOException
	 */
	public void close(Closeable closeable) throws IOException{
		if(null != closeable){
			closeable.close();
		}
	}
}
