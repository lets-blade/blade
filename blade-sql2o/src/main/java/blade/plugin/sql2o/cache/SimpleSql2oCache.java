package blade.plugin.sql2o.cache;

import java.util.List;

import blade.cache.Cache;
import blade.cache.CacheManager;
import blade.plugin.sql2o.Model;

@SuppressWarnings("unchecked")
public class SimpleSql2oCache<T extends Model> implements Sql2oCache<T> {

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
	public void set(String key, T value) {
		cache.set(key, value);
	}

	@Override
	public void set(String key, T value, long expire) {
		cache.set(key, value, expire);
	}

	@Override
	public void hset(String key, String field, T value) {
		cache.hset(key, field, value);
	}

	@Override
	public void hset(String key, String field, T value, long expire) {
		cache.hset(key, field, value, expire);
	}

	@Override
	public void hset(String key, String field, List<T> value, long expire) {
		cache.hset(key, field, value, expire);
	}

	@Override
	public T get(String key) {
		Object value = cache.get(key);
		if(null != value){
			return (T) value;
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
	public <V> void hsetV(String key, String field, V value) {
		cache.hset(key, field, value);
	}

	@Override
	public <M extends Model> M hget(String key, String field) {
		Object object = cache.hget(key, field);
		if(null != object){
			return (M) object;
		}
		return null;
	}
	

	@Override
	public <M extends Model> List<M> hgetlist(String key, String field) {
		Object object = cache.hget(key, field);
		if(null != object){
			return (List<M>) object;
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
	public <M extends Model> void hsetlist(String key, String field, List<M> value) {
		cache.hset(key, field, value);
	}

}
