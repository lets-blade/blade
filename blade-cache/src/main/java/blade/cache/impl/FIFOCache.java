package blade.cache.impl;

import java.util.Iterator;

import blade.cache.AbstractCache;
import blade.cache.CacheObject;


/**
 * FIFO实现
 * 
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 * @param <K>
 * @param <V>
 */
public class FIFOCache<K, V> extends AbstractCache<K, V> {

	public FIFOCache(int cacheSize) {
		super(cacheSize);
	}

	@Override
	protected int eliminateCache() {

		int count = 0;
		K firstKey = null;

		Iterator<CacheObject<K, V>> iterator = _mCache.values().iterator();
		while (iterator.hasNext()) {
			CacheObject<K, V> cacheObject = iterator.next();

			if (cacheObject.isExpired()) {
				iterator.remove();
				count++;
			} else {
				if (firstKey == null)
					firstKey = cacheObject.getKey();
			}
		}

		if (firstKey != null && isFull()) {// 删除过期对象还是满,继续删除链表第一个
			_mCache.remove(firstKey);
		}

		return count;
	}

}