package blade.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import blade.cache.impl.FIFOCache;
import blade.cache.impl.LFUCache;
import blade.cache.impl.LRUCache;

/**
 * 缓存管理对象
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class CacheManager {
	
	private static final CacheManager _instance = new CacheManager();
	
	private static final String CACHE_SUFFIX = "blade_cache_ID_";
	
	private Map<String, Cache> _cacheMap;
	
	private CacheCleaner _cleaner;
	
    private int _cacheSize = 100;
    
    // 一小时
    private static final int DEFAULT_CLEAN_TIME = 3600000;
    
    private static Object _mlock = new Object(); 
    
    public CacheManager() {
    	_cacheMap = new HashMap<String, Cache>();
    	_cleaner = new CacheCleaner(DEFAULT_CLEAN_TIME); //默认30秒自动清空缓存
    	_cleaner.start();
    }
    
	public static CacheManager getInstance(){
		return  _instance;
	}
	
	public void setCleanInterval(long time) {
        _cleaner.setCleanInterval(time);
    }
	
	public <K, V> Cache<K, V> getCache(String cacheId){
		if (cacheId == null) {
			throw new NullPointerException("cacheId is null");
		}
		synchronized (_mlock) {
			return _cacheMap.get(cacheId);
		}
	}
	
	/***************************** LRUCache:START **************************************/
	public <K, V> Cache<K, V> newLRUCache(){
		synchronized (_mlock) {
			String cacheId = CACHE_SUFFIX + System.currentTimeMillis();
			return newLRUCache(cacheId, _cacheSize);
		}
	}
	
	public <K, V> Cache<K, V> newLRUCache(String cacheId){
		if (cacheId == null) {
			throw new NullPointerException("cacheId is null");
		}
		return newLRUCache(cacheId, _cacheSize);
	}
	
	public <K, V> Cache<K, V> newLRUCache(int cacheSize){
		synchronized (_mlock) {
			String cacheId = CACHE_SUFFIX + System.currentTimeMillis();
			return newLRUCache(cacheId, cacheSize);
		}
	}
	
	public <K, V> Cache<K, V> newLRUCache(String cacheId, int cacheSize){
		synchronized (_mlock) {
			Cache<K, V> cache = new LRUCache<K, V>(cacheSize);
			_cacheMap.put(cacheId, cache);
			return cache;
		}
	}
	/***************************** LRUCache:END **************************************/
	
	
	/***************************** LFUCache:START **************************************/
	public <K, V> Cache<K, V> newLFUCache(){
		synchronized (_mlock) {
			String cacheId = CACHE_SUFFIX + System.currentTimeMillis();
			return newLFUCache(cacheId, _cacheSize);
		}
	}
	
	public <K, V> Cache<K, V> newLFUCache(String cacheId){
		if (cacheId == null) {
			throw new NullPointerException("cacheId is null");
		}
		return newLFUCache(cacheId, _cacheSize);
	}
	
	public <K, V> Cache<K, V> newLFUCache(int cacheSize){
		synchronized (_mlock) {
			String cacheId = CACHE_SUFFIX + System.currentTimeMillis();
			return newLFUCache(cacheId, cacheSize);
		}
	}
	
	public <K, V> Cache<K, V> newLFUCache(String cacheId, int cacheSize){
		synchronized (_mlock) {
			Cache<K, V> cache = new LFUCache<K, V>(cacheSize);
			_cacheMap.put(cacheId, cache);
			return cache;
		}
	}
	/***************************** LFUCache:END **************************************/
	
	
	/***************************** LFUCache:START **************************************/
	public <K, V> Cache<K, V> newFIFOCache(){
		synchronized (_mlock) {
			String cacheId = CACHE_SUFFIX + System.currentTimeMillis();
			return newFIFOCache(cacheId, _cacheSize);
		}
	}
	
	public <K, V> Cache<K, V> newFIFOCache(String cacheId){
		if (cacheId == null) {
			throw new NullPointerException("cacheId is null");
		}
		return newLFUCache(cacheId, _cacheSize);
	}
	
	public <K, V> Cache<K, V> newFIFOCache(int cacheSize){
		synchronized (_mlock) {
			String cacheId = CACHE_SUFFIX + System.currentTimeMillis();
			return newFIFOCache(cacheId, cacheSize);
		}
	}
	
	public <K, V> Cache<K, V> newFIFOCache(String cacheId, int cacheSize){
		synchronized (_mlock) {
			Cache<K, V> cache = new FIFOCache<K, V>(cacheSize);
			_cacheMap.put(cacheId, cache);
			return cache;
		}
	}
	/***************************** LFUCache:END **************************************/
	
	/**
	 * @return	返回所有缓存id
	 */
	public Set<String> getCacheIds(){
		synchronized (_mlock) {
			if(null != _cacheMap && _cacheMap.size() > 0){
				return _cacheMap.keySet();
			}
		}
		return null;
	}
	
	/**
	 * 移除一个缓存
	 * @param cacheId
	 * @throws CacheException
	 */
    public void removeCache(String cacheId) throws CacheException {
        if(cacheId == null) {
            throw new NullPointerException("cacheId is null");
        }
        synchronized(_mlock){
            _cacheMap.remove(cacheId);
        }
    }
    
    /**
	 * 移除所有缓存
	 * @param cacheId
	 * @throws CacheException
	 */
	public <K, V> void removeAll() {
        synchronized(_mlock){
        	if(null != _cacheMap && _cacheMap.size() > 0){
        		
        		Set<String> keys = _cacheMap.keySet();
				for(String key : keys){
					Cache<K, V> cache = _cacheMap.get(key);
					if(null != cache){
						cache.clear();
					}
				}
				_cacheMap.clear();
			}
        }
    }
}