package blade.kit.http;

/**
 * http异常封装
 * <p>
 * </p>
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class HttpException extends RuntimeException {

	private static final long serialVersionUID = -6657642586184082657L;

	/**
	 * HttpException
	 */
	public HttpException() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * HttpException
	 * @param msg
	 */
	public HttpException(String msg) {
		super(msg);
	}
	
	/**
	 * HttpException
	 * @param t
	 */
	public HttpException(Throwable t) {
		super(t);
	}
}
