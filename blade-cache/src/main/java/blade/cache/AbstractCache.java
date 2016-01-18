/**
 * Copyright (c) 2015, biezhi 王爵 (biezhi.me@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package blade.cache;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 
 * <p>
 * Abstract cache basic implementation
 * </p>
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
@SuppressWarnings("unchecked")
public abstract class AbstractCache<K, V> implements Cache<K, V> {

	/**
	 * Sync cache container
	 */
	protected Map<K, CacheObject<K, V>> _mCache;
	
	/**
	 * Sync cache container
	 */
	protected Map<K, Map<?, CacheObject<K, V>>> _hCache;
	
	/**
	 * Cache lock
	 */
	protected final ReentrantReadWriteLock cacheLock = new ReentrantReadWriteLock();
	
	/**
	 * Read lock
	 */
	protected final Lock readLock = cacheLock.readLock();
    
	/**
	 * Write lock
	 */
	protected final Lock writeLock = cacheLock.writeLock();
	
	/**
	 * Cache size
	 */
	protected int cacheSize;
	
	/**
	 * 默认过期时间, 0 -> 永不过期
	 * Default expire interval, 0 -> never expired
	 */
	protected long defaultExpire;
	
	/**
	 * 是否设置默认过期时间
	 * Whether set custom expire time
	 */
	protected  boolean existCustomExpire;

	/**
	 * Concrete implementation of eliminate a cache
     * @return
     */
    protected abstract int eliminateCache(); 
    
    /**
	 * Set cache size and initialize
     * @param cacheSize
     */
	public AbstractCache(int cacheSize) {
		this.cacheSize	= cacheSize;
		this._mCache	= Collections.synchronizedMap(new HashMap<K, CacheObject<K, V>>());
		this._hCache	= Collections.synchronizedMap(new HashMap<K, Map<?, CacheObject<K, V>>>());
	}
	
	/**
	 * Set a cache object
	 */
	public void set(K key, V obj) {
		set(key, obj, defaultExpire);
	}

	/**
	 * Set a cache object with expire time
	 */
	public void set(K key, V value, long expire) {
		writeLock.lock();
		try {
            CacheObject<K,V> co = new CacheObject<K,V>(key, value, expire);
            if (expire != 0) {
                existCustomExpire = true;
            }
            if (isFull()) {
                eliminate() ;
            }
            _mCache.put(key, co);
        }
        finally {
            writeLock.unlock();
        }
	}
	
	/**
	 * Set a hashed cache object
	 */
	public <F> void hset(K key, F field, V obj) {
		hset(key, field, obj, defaultExpire);
	}
	
	/**
	 * Set a hash cache object with expire time
	 */
	public <F> void hset(K key, F field, V value, long expire) {
		writeLock.lock();
		try {
			CacheObject<K, V> co = new CacheObject<K, V>(key, value, expire);
			
			if(expire != 0){
				existCustomExpire = true;
			}
			
			if(isFull()){
				eliminate() ;
			}
			
			Map<F, CacheObject<K, V>> coMap = (Map<F, CacheObject<K, V>>) _hCache.get(key);
			if(null == coMap){
				coMap = new HashMap<F, CacheObject<K,V>>();
			}
			coMap.put(field, co);
			
			_hCache.put(key, coMap);
        }
        finally {
            writeLock.unlock();
        }
	}
	
	
	/**
	 * Get a cache object
	 */
	public V get(K key) {
		readLock.lock();
        try {
            CacheObject<K,V> co = _mCache.get(key);
            if (co == null) {
                return null;
            }
            if (co.isExpired() == true) {
            	_mCache.remove(key);
                return null;
            }
            return co.getValue();
        } finally {
            readLock.unlock();
        }
	}
	
	/**
	 * Get a hash cache object
	 */
	public <F> V hget(K key, F field) {
		readLock.lock();
        try {
        	Map<?, CacheObject<K, V>> coMap = _hCache.get(key);
        	
        	if(null == coMap){
        		return null;
        	}
        	
        	CacheObject<K, V> co = coMap.get(field);
        	
        	if(null == co){
        		return null;
        	}
        	
			if (co.isExpired() == true) {
				coMap.remove(field);
				return null;
			}
			
			return co.getValue();
        } finally {
            readLock.unlock();
        }
	}

	/**
	 * Remove a cache object
	 */
	public void del(K key) {
		writeLock.lock();
        try {
            _mCache.remove(key);
        } finally {
            writeLock.unlock();
        }
	}
	
	/**
	 * Remove a hash cache object
	 */
	public void hdel(K key) {
		writeLock.lock();
        try {
        	_hCache.remove(key);
        } finally {
            writeLock.unlock();
        }
	}
	
	/**
	 * Remove a cache object
	 */
	public <F> void del(K key, F feild) {
		writeLock.lock();
        try {
        	Map<?, CacheObject<K, V>> coMap = _hCache.get(key);
        	if(null != coMap){
        		coMap.remove(feild);
        	}
        } finally {
            writeLock.unlock();
        }
	}
	
	@Override
	public Set<K> keys() {
		return _mCache.keySet();
	}
	
	@Override
	public <F> Set<F> flieds(K key) {
		Map<?, CacheObject<K, V>> coMap = _hCache.get(key);
		if(null == coMap){
			return null;
		}
		return (Set<F>) coMap.keySet();
	}
	
	public int elementsInCache() {
		return ( _mCache.size() + _hCache.size() );
	}
	
	@Override
	public int size() {
		return ( _mCache.size() + _hCache.size() );
	}

	protected boolean isNeedClearExpiredObject(){
        return defaultExpire > 0 || existCustomExpire ;
    }
	
	public final int eliminate() {
		writeLock.lock();
        try {
            return eliminateCache();
        }
        finally {
            writeLock.unlock();
        }
	}

	@Override
	public boolean isFull() {
		if (cacheSize == 0) {// o -> 无限制
			return false;
		}
		return ( _mCache.size() + _hCache.size() ) >= cacheSize;
	}

	@Override
	public void clear() {
		writeLock.lock();
        try {
        	_mCache.clear();
        } finally {
            writeLock.unlock();
        }
	}

	@Override
	public int getCacheSize() {
		return cacheSize;
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public Cache<K, V> cacheSize(int cacheSize) {
		this.cacheSize = cacheSize;
		return this;
	}
	
	@Override
	public Cache<K, V> expire(long expire) {
		this.defaultExpire = expire;
		return this;
	}
	
}