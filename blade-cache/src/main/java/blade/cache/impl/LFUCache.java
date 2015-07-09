package blade.cache.impl;

import java.util.Iterator;

import blade.cache.AbstractCache;
import blade.cache.CacheObject;

/**
 * LFU实现
 *
 * @author <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.0
 * @param <K>
 * @param <V>
 */
public class LFUCache<K, V> extends AbstractCache<K, V> {
	
	public LFUCache(int cacheSize) {
		super(cacheSize);
	}

	/**
	 * 实现删除过期对象 和 删除访问次数最少的对象
	 * 
	 */
	@Override
	protected int eliminateCache() {
		Iterator<CacheObject<K, V>> iterator = _mCache.values().iterator();
		int count = 0;
		long minAccessCount = Long.MAX_VALUE;
		
		while (iterator.hasNext()) {
			CacheObject<K, V> cacheObject = iterator.next();

			if (cacheObject.isExpired()) {
				iterator.remove();
				count++;
				continue;
			} else {
				minAccessCount = Math.min(cacheObject.getAccessCount(),
						minAccessCount);
			}
		}

		if (count > 0)
			return count;

		if (minAccessCount != Long.MAX_VALUE) {

			iterator = _mCache.values().iterator();

			while (iterator.hasNext()) {
				CacheObject<K, V> cacheObject = iterator.next();

				cacheObject.setAccessCount(cacheObject.getAccessCount() - minAccessCount);

				if (cacheObject.getAccessCount() <= 0) {
					iterator.remove();
					count++;
				}

			}

		}

		return count;
	}

}