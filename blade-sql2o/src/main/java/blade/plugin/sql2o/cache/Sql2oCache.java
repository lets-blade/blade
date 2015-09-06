package blade.plugin.sql2o.cache;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import blade.cache.CacheException;

public interface Sql2oCache {

	void hset(String key, String field, Serializable value);
	
	void hsetV(String key, String field, Object value);
	
	<T extends Serializable> void hsetlist(String key, String field, List<T> value);
	
	void hsetlistmap(String key, String field, List<Map<String, Object>> value);
	
	<S> void hsetlists(String key, String field, List<S> value);
	
	<T extends Serializable> T hget(String key, String field);
	
	<V> V hgetV(String key, String field);
	
	<T extends Serializable> List<T> hgetlist(String key, String field);
	
	List<Map<String, Object>> hgetlistmap(String key, String field);
	
	<S> List<S> hgetlists(String key, String field);
	
	void hdel(String key);
	
	void clean();
	
	void destroy() throws CacheException;
}
