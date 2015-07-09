package blade.cache;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 抽象缓存基础实现
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 * @param <K>
 * @param <V>
 */
@SuppressWarnings("unchecked")
public abstract class AbstractCache<K, V> implements Cache<K, V> {

	/**
	 * 同步缓存容器
	 */
	protected Map<K, CacheObject<K, V>> _mCache;
	
	/**
	 * 同步缓存容器
	 */
	protected Map<K, Map<?, CacheObject<K, V>>> _hCache;
	
	/**
	 * 缓存锁
	 */
	protected final ReentrantReadWriteLock cacheLock = new ReentrantReadWriteLock();
	
	/**
	 * 读取锁
	 */
	protected final Lock readLock = cacheLock.readLock();
    
	/**
	 * 写入锁
	 */
	protected final Lock writeLock = cacheLock.writeLock();
	
	/**
	 * 最大缓存数
	 */
	protected int cacheSize;
	
	/**
	 * 默认过期时间, 0 -> 永不过期
	 */
	protected long defaultExpire;
	
	/**
	 * 是否设置默认过期时间
	 */
	protected  boolean existCustomExpire;

	/**
     * 淘汰对象具体实现
     * @return
     */
    protected abstract int eliminateCache(); 
    
    /**
     * 设置一个缓存大小并初始化
     * @param cacheSize
     */
	public AbstractCache(int cacheSize) {
		this.cacheSize	= cacheSize;
		this._mCache	= Collections.synchronizedMap(new HashMap<K, CacheObject<K, V>>());
		this._hCache	= Collections.synchronizedMap(new HashMap<K, Map<?, CacheObject<K, V>>>());
	}
	
	/**
	 * 放一个缓存
	 */
	public void set(K key, V obj) {
		set(key, obj, defaultExpire);
	}

	/**
	 * 放一个缓存并设置缓存时间
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
	 * 放一个缓存
	 */
	public <F> void hset(K key, F field, V obj) {
		hset(key, field, obj, defaultExpire);
	}
	
	/**
	 * 放一个hash类型缓存并设置缓存时间
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
	 * 取一个缓存
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
	 * 取一个缓存
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
	 * 移除一个缓存
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
	 * 移除一个缓存
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
	 * 移除一个缓存
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