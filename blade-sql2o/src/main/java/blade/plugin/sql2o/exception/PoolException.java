package blade.plugin.sql2o.exception;


/**
 * PoolException
 * <p>
 * </p>
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class PoolException extends RuntimeException{

	private static final long serialVersionUID = 4566581404090220394L;

	public PoolException() {
		throw new RuntimeException();
	}
	
	public PoolException(String e) {
		throw new RuntimeException(e);
	}
	
	public PoolException(Exception e) {
		throw new RuntimeException(e);
	}
}
