package blade.cache;

import java.util.Set;

/**
 * 定时清理缓存线程
 *
 * @author <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.0
 */
public class CacheCleaner<K, V> extends Thread {
	
	/**
	 * 清理间隔
	 */
	private long _cleanInterval;
	
	/**
	 * 是不是在睡眠中
	 */
	private boolean _sleep = false;
	
	/**
	 * 设置清理间隔并初始化
	 * 
	 * @param cleanInterval
	 */
	public CacheCleaner(long cleanInterval) {
		_cleanInterval = cleanInterval;
		setName(this.getClass().getName());
		setDaemon(true);
	}

	/**
	 * 设置清理间隔
	 * 
	 * @param cleanInterval
	 */
	public void setCleanInterval(long cleanInterval) {
		_cleanInterval = cleanInterval;
		synchronized (this) {
			if (_sleep) {
				interrupt();
			}
		}
	}

	/**
	 * 执行缓存清理工作
	 */
	@Override
	public void run() {
		while (true) {
			try {
				try {
					sleep(_cleanInterval);
				} catch (Throwable t) {
				} finally {
					_sleep = false;
				}
				CacheManager cacheFactory = CacheManager.getInstance();
				Set<String> cacheIds = cacheFactory.getCacheIds();
				if (null != cacheIds) {
					for (String cacheId : cacheIds) {
						Cache<K, V> cache = cacheFactory.getCache(cacheId);
						if (cache != null) {
							cache.clear();
						}
						yield();
					}
				}
			} catch (Throwable t) {
				t.printStackTrace();
			}
			_sleep = true;
//			try {
//				sleep(_cleanInterval);
//			} catch (Throwable t) {
//			} finally {
//				_sleep = false;
//			}
		}
	}

}