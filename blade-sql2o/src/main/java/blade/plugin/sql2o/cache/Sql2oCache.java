package blade.plugin.sql2o.cache;

import java.util.List;

import blade.plugin.sql2o.Model;

public interface Sql2oCache<T extends Model> {

	void set(String key, T value);
	
	void set(String key, T value, long expire);
	
	void hset(String key, String field, T value);
	
	<V> void hsetV(String key, String field, V value);
	
	<M extends Model> void hsetlist(String key, String field, List<M> value);
	
	void hset(String key, String field, T value, long expire);
	
	void hset(String key, String field, List<T> value, long expire);
	
	<M extends Model> M get(String key);
	
	<M extends Model> M hget(String key, String field);
	
	<V> V hgetV(String key, String field);
	
	<M extends Model> List<M> hgetlist(String key, String field);
	
	void hdel(String key);
	
	void hdel(String key, String field);
	
}
