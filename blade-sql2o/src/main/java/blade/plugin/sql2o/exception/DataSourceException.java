package blade.plugin.sql2o.exception;


/**
 * DataSourceException
 * <p>
 * </p>
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class DataSourceException extends RuntimeException{

	private static final long serialVersionUID = 4566581404090220394L;

	public DataSourceException() {
		throw new RuntimeException();
	}
	
	public DataSourceException(String e) {
		throw new RuntimeException(e);
	}
	
	public DataSourceException(Exception e) {
		throw new RuntimeException(e);
	}
}
