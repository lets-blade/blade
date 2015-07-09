package blade.kit.http;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.List;
import java.util.Map;

/**
 * http请求类
 * <p>
 * </p>
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class HttpRequest extends HttpBase<HttpRequest>{

	/**
	 * method
	 */
	protected String method = "GET";
	
	/**
	 * URL
	 */
	private String url = "";
	
	/**
	 * 默认超时
	 */
	private int timeout = -1;
	
	/**
	 * 连接对象
	 */
	private HttpConnectionWrapper httpConnection;
	
	/**
	 * 设置请求方法
	 * @param method
	 * @return HttpRequest
	 */
	private HttpRequest method(String method) {
		this.method = method.toUpperCase();
		return this;
	}
	
	/**
	 * 设置请求URL
	 * @param url
	 */
	public HttpRequest(String url) {
		this.url = url;
	}
	
	/**
	 * POST请求
	 * @param url
	 * @return HttpRequest
	 */
	public static HttpRequest post(String url) {
		return new HttpRequest(url).method("POST");
	}
	
	/**
	 * GET请求
	 * @param url
	 * @return HttpRequest
	 */
	public static HttpRequest get(String url) {
		return new HttpRequest(url).method("GET");
	}
	
	/**
	 * 设置超时
	 * @param milliseconds
	 * @return HttpRequest
	 */
	public HttpRequest timeout(int milliseconds) {
		this.timeout = milliseconds;
		return this;
	}
	
	/**
	 * 执行Reuqest请求 
	 * @return HttpResponse
	 * @throws IOException 
	 * @throws NoSuchProviderException 
	 * @throws NoSuchAlgorithmException 
	 * @throws KeyManagementException 
	 */
	public HttpResponse execute() throws Exception{
		
		if(this.method.equalsIgnoreCase("get")){
			withUrl();
		}
		// init connection
		this.httpConnection = new HttpConnectionWrapper(this.url, this.method);
		
		// response
		HttpResponse httpResponse = null;
		if(this.timeout != -1){
			// connect timeout
			this.httpConnection.setConnectTimeout(this.timeout);
			// read timeout
			this.httpConnection.setReadTimeout(this.timeout);
		}
		
		// set header
		if(!this.headers.isEmpty()){
			this.httpConnection.setRequestProperty(this.headers);
		}
		
		try {
			
			if(this.method.equalsIgnoreCase("POST")){
				OutputStream outputStream = httpConnection.getOutputStream();
				sendTo(outputStream);
			} else {
				this.httpConnection.connect();
			}
			
			InputStream inputStream = this.httpConnection.getInputStream();
			Map<String, List<String>> headerFields = this.httpConnection.getHeaderFields();
			
			httpResponse = HttpResponse.readResponse(inputStream);
			
			httpResponse.setHeader(headerFields);
			httpResponse.setStatusCode(this.httpConnection.getResponseCode());
			httpResponse.setHttpRequest(this);
			
		} catch (IOException ioex) {
			throw new HttpException(ioex);
		}
		boolean keepAlive = httpResponse.isConnectionPersistent();
		if (keepAlive == false) {
			// closes connection if keep alive is false, or if counter reached 0
			this.httpConnection.disconnect();
			this.httpConnection = null;
		}
		return httpResponse;
	}
	
	/**
	 * 转换URL
	 */
	private void withUrl(){
		String queryString = HttpKit.getQuery(this.form);
		if(null != queryString && !"".equals(queryString)){
			if(this.url.endsWith("?")){
				this.url += queryString;
			} else {
				this.url += "?" + queryString;
			}
		}
	}
	
	/**
	 * 发送数据流
	 * @param outputStream
	 * @throws IOException
	 */
	private void sendTo(OutputStream outputStream) throws IOException {
		if(null != outputStream){
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, this.formEncoding));
			String queryString = HttpKit.getQuery(this.form);
			writer.write(queryString);
			writer.flush();
			writer.close();
			
			outputStream.close();
		}
	}
	
}
