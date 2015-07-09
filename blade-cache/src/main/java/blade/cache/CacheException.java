package blade.cache;

/**
 * 缓存异常封装
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class CacheException extends Exception {

	private static final long serialVersionUID = 2794445171318574075L;

	public CacheException() {
	}

	public CacheException(String msg) {
		super(msg);
	}
}