package blade.kit.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * http连接对象包装
 * <p>
 * </p>
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class HttpConnectionWrapper {

	/**
	 * httpsURLConnection
	 */
	private HttpsURLConnection httpsURLConnection;
	
	/**
	 * httpURLConnection
	 */
	private HttpURLConnection httpURLConnection;
	
	/**
	 * timeout超时，以毫秒为单位
	 */
	private int timeout;
	
	/**
	 * url
	 */
	private String url;
	
	/**
	 * method请求方法
	 */
	private String method = "GET";

	/**
	 * 初始化HttpConnection
	 * @param url
	 * @param method
	 * @throws IOException
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 */
	public HttpConnectionWrapper(String url, String method) throws IOException, KeyManagementException, NoSuchAlgorithmException, NoSuchProviderException {
		this.url = url;
		this.method = method;
		
		if(isHttps(this.url)){
			initHttps();
		} else {
			initHttp();
		}
	}

	/**
	 * 初始化http请求参数
	 */
	private void initHttp() throws IOException {
		URL _url = new URL(this.url);
		this.httpURLConnection = (HttpURLConnection) _url.openConnection();
		
		// method
		if (this.method.equalsIgnoreCase("post")) {
			this.httpURLConnection.setDoOutput(true);
			this.httpURLConnection.setUseCaches(false);
		}
		this.httpURLConnection.setDoInput(true);
		this.httpURLConnection.setRequestMethod(this.method);
		
		// header
		this.httpURLConnection.setRequestProperty(HttpBase.HEADER_ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		this.httpURLConnection.setRequestProperty(HttpBase.HEADER_CONTENT_TYPE, "application/x-www-form-urlencoded");
		this.httpURLConnection.setRequestProperty(HttpBase.HEADER_USER_AGENT, "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:36.0) Gecko/20100101 Firefox/36.0");
		
		this.httpsURLConnection = null;

	}

	/**
	 * 初始化http请求参数
	 */
	private void initHttps() throws IOException, NoSuchAlgorithmException, NoSuchProviderException, KeyManagementException {
		TrustManager[] tm = { new MyX509TrustManager() };
		SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
		sslContext.init(null, tm, new java.security.SecureRandom());
		
		// 从上述SSLContext对象中得到SSLSocketFactory对象
		SSLSocketFactory ssf = sslContext.getSocketFactory();
		
		URL _url = new URL(url);
		this.httpsURLConnection = (HttpsURLConnection) _url.openConnection();
		
		// domain validate
		this.httpsURLConnection.setHostnameVerifier(new TrustAnyHostnameVerifier());

		this.httpsURLConnection.setSSLSocketFactory(ssf);
		
		// method
		if (this.method.equalsIgnoreCase("post")) {
			this.httpsURLConnection.setDoOutput(true);
			this.httpsURLConnection.setUseCaches(false);
		}
		this.httpsURLConnection.setDoInput(true);
		this.httpsURLConnection.setRequestMethod(this.method);
		
		// header
		this.httpsURLConnection.setRequestProperty(HttpBase.HEADER_ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		this.httpsURLConnection.setRequestProperty(HttpBase.HEADER_CONTENT_TYPE, "application/x-www-form-urlencoded");
		this.httpsURLConnection.setRequestProperty(HttpBase.HEADER_USER_AGENT, "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:36.0) Gecko/20100101 Firefox/36.0");
		
		this.httpURLConnection = null;
	}
	
	/**
	 * 获取请求方法,GET/POST
	 * @return String
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * 设置请求方法
	 * @param method
	 */
	public void setMethod(String method) {
		this.method = method;
	}

	/**
	 * 获取请求URL
	 * @return String
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * 设置请求URL
	 * @param url
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * 获取超时
	 * @return int
	 */
	public int getTimeout() {
		return timeout;
	}

	/**
	 * 设置超时
	 * @param timeout
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	/**
	 * 获取HttpsURLConnection对象
	 * @return HttpsURLConnection
	 */
	public HttpsURLConnection getHttpsURLConnection() {
		return httpsURLConnection;
	}
	
	/**
	 * 获取HttpURLConnection对象
	 * @return HttpURLConnection
	 */
	public HttpURLConnection getHttpURLConnection() {
		return httpURLConnection;
	}

	/**
	 * 检测是否https
	 * @param url
	 * @return boolean
	 */
	private boolean isHttps(String url) {
		return url.startsWith("https");
	}

	/**
	 * https 域名校验
	 * 
	 * @author biezhi
	 * @since 1.0
	 */
	public class TrustAnyHostnameVerifier implements HostnameVerifier {
		public boolean verify(String hostname, SSLSession session) {
			return true;// 直接返回true
		}
	}
	
	/**
	 * 设置连接超时
	 * @param timeout
	 */
	public void setConnectTimeout(int timeout){
		if(null != this.httpURLConnection){
			this.httpURLConnection.setConnectTimeout(timeout);
		}
		if(null != this.httpsURLConnection){
			this.httpsURLConnection.setConnectTimeout(timeout);
		}
	}
	
	/**
	 * 设置读取超时
	 * @param timeout
	 */
	public void setReadTimeout(int timeout){
		if(null != this.httpURLConnection){
			this.httpURLConnection.setReadTimeout(timeout);
		}
		if(null != this.httpsURLConnection){
			this.httpsURLConnection.setReadTimeout(timeout);
		}
	}
	
	/**
	 * 设置请求头
	 * @param key
	 * @param value
	 */
	public void setRequestProperty(String key, String value){
		if(null != this.httpURLConnection){
			this.httpURLConnection.setRequestProperty(key, value);
		}
		if(null != this.httpsURLConnection){
			this.httpsURLConnection.setRequestProperty(key, value);
		}
	}
	
	/**
	 * 设置请求头
	 * @param headers
	 */
	public void setRequestProperty(Map<String, List<String>> headers){
		Set<String> keySet = headers.keySet();
		for(String key : keySet){
			List<String> valueList = headers.get(key);
			this.setRequestProperty(key, valueList.get(0));
		}
	}
	
	/**
	 * 连接
	 * @throws IOException
	 */
	public void connect() throws IOException{
		if(null != this.httpURLConnection){
			this.httpURLConnection.connect();
		}
		if(null != this.httpsURLConnection){
			this.httpsURLConnection.connect();
		}
	}
	
	/**
	 * 断开连接
	 */
	public void disconnect(){
		if(null != this.httpURLConnection){
			this.httpURLConnection.disconnect();
		}
		if(null != this.httpsURLConnection){
			this.httpsURLConnection.disconnect();
		}
	}
	
	public InputStream getInputStream() throws IOException{
		if(null != this.httpURLConnection){
			return this.httpURLConnection.getInputStream();
		}
		if(null != this.httpsURLConnection){
			return this.httpsURLConnection.getInputStream();
		}
		return null;
	}
	
	/**
	 * 获取输出流对象 
	 * @return OutputStream
	 * @throws IOException
	 */
	public OutputStream getOutputStream() throws IOException{
		if(null != this.httpURLConnection){
			return this.httpURLConnection.getOutputStream();
		}
		if(null != this.httpsURLConnection){
			return this.httpsURLConnection.getOutputStream();
		}
		return null;
	}
	
	/**
	 * 获取请求头信息
	 * @return Map<String, List<String>>
	 */
	public Map<String, List<String>> getHeaderFields(){
		if(null != this.httpURLConnection){
			return this.httpURLConnection.getHeaderFields();
		}
		if(null != this.httpsURLConnection){
			return this.httpsURLConnection.getHeaderFields();
		}
		return null;
	}
	
	/**
	 * 获取响应码
	 * @return int
	 * @throws IOException
	 */
	public int getResponseCode() throws IOException{
		if(null != this.httpURLConnection){
			return this.httpURLConnection.getResponseCode();
		}
		if(null != this.httpsURLConnection){
			return this.httpsURLConnection.getResponseCode();
		}
		return 0;
	}
}

// 证书管理
class MyX509TrustManager implements X509TrustManager {

	public X509Certificate[] getAcceptedIssuers() {
		return null;
	}

	@Override
	public void checkClientTrusted(X509Certificate[] chain, String authType)
			throws CertificateException {
	}

	@Override
	public void checkServerTrusted(X509Certificate[] chain, String authType)
			throws CertificateException {
	}
}