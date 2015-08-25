package blade.plugin.sql2o.cache;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import blade.cache.Cache;
import blade.cache.CacheManager;

@SuppressWarnings("unchecked")
public class SimpleSql2oCache implements Sql2oCache {

	private CacheManager cm = CacheManager.getInstance();
	
	private Cache<String, Object> cache;
	
	public SimpleSql2oCache() {
		// 5小时清理一次
		long cleanTime = 1000 * 3600 * 300;
		cm.setCleanInterval(cleanTime);
		// 存放1000个缓存，超过即自动清除
		cache = cm.newLRUCache("sql2o_cache").cacheSize(1000);
	}
	
	@Override
	public void set(String key, Serializable value) {
		cache.set(key, value);
	}

	@Override
	public void set(String key, Serializable value, long expire) {
		cache.set(key, value, expire);
	}

	@Override
	public void hset(String key, String field, Serializable value) {
		cache.hset(key, field, value);
	}

	@Override
	public void hset(String key, String field, Serializable value, long expire) {
		cache.hset(key, field, value, expire);
	}

	@Override
	public void hset(String key, String field, List<Serializable> value, long expire) {
		cache.hset(key, field, value, expire);
	}

	@Override
	public Serializable get(String key) {
		Object value = cache.get(key);
		if(null != value){
			return (Serializable) value;
		}
		return null;
	}
	
	@Override
	public <V> V hgetV(String key, String field) {
		Object value = cache.hget(key, field);
		if(null != value){
			return (V) value;
		}
		return null;
	}
	
	@Override
	public void hsetV(String key, String field, Object value) {
		cache.hset(key, field, value);
	}

	@Override
	public Serializable hget(String key, String field) {
		Object object = cache.hget(key, field);
		if(null != object){
			return (Serializable) object;
		}
		return null;
	}
	

	@Override
	public List<Serializable> hgetlist(String key, String field) {
		Object object = cache.hget(key, field);
		if(null != object){
			return (List<Serializable>) object;
		}
		return null;
	}
	
	@Override
	public List<Map<String, Object>> hgetlistmap(String key, String field) {
		Object object = cache.hget(key, field);
		if(null != object){
			return (List<Map<String, Object>>) object;
		}
		return null;
	}
	
	@Override
	public <S> List<S> hgetlists(String key, String field) {
		Object object = cache.hget(key, field);
		if(null != object){
			return (List<S>) object;
		}
		return null;
	}

	@Override
	public void hdel(String key) {
		cache.hdel(key);
	}

	@Override
	public void hdel(String key, String field) {
		cache.del(key, field);
	}

	@Override
	public <T extends Serializable>  void hsetlist(String key, String field, List<T> value) {
		cache.hset(key, field, value);
	}
	
	@Override
	public void hsetlistmap(String key, String field, List<Map<String, Object>> value) {
		cache.hset(key, field, value);
	}
	
	@Override
	public <S> void hsetlists(String key, String field, List<S> value) {
		cache.hset(key, field, value);
	}
	
	@Override
	public void clean() {
		cache.clear();
	}

}
