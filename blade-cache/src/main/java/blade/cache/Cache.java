
package blade.cache;

import java.util.Set;


/**
 * 缓存顶级接口
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 * @param <K>
 * @param <V>
 */
public interface Cache<K, V> {
 
	/**
	 * 向缓存添加value对象,其在缓存中生存时间为默认值
	 * 
	 * @param key
	 * @param value
	 * @throws CacheException
	 */
    void set(K key, V value);
    
    /**
     * 向缓存添加value对象,并指定存活时间
     * 
     * @param key
     * @param value	过期时间
     * @param expire
     * @throws CacheException
     */
    void set(K key, V value, long expire);
    
    /**
     * 向缓存添加一个hash类型的数据
     * 
     * @param key
     * @param field
     * @param value
     */
    <F> void hset(K key, F field, V value);
    
    /**
     * 向缓存添加一个hash类型的数据并设置有效期
     * 
     * @param key
     * @param field
     * @param value
     * @param expire
     */
    <F> void hset(K key, F field, V value, long expire);
    
    /**
     * 查找缓存对象
     * 
     * @param key
     * @return
     * @throws CacheException
     */
    V get(K key);
    
    /**
     * 查找缓存对象
     * 
     * @param key
     * @param field
     * @return
     */
    <F> V hget(K key, F field);
    
    /**
     * 删除缓存对象
     * 
     * @param key
     */
    void del(K key);
    
    /**
     * 删除缓存对象
     * 
     * @param key
     */
    void hdel(K key);
    
    /**
     * 删除缓存对象
     * 
     * @param key
     * @param field
     */
    <F> void del(K key, F field);
    
    /**
     * @return	返回所有key
     */
    Set<K> keys();
    
    /**
     * @return	返回一个key所有flied
     */
    <F> Set<F> flieds(K key);
    
    /**
     * @return	返回当前缓存的大小
     */
    int size();
    
    /**
     * 淘汰对象
     * 
     * @return  被删除对象大小
     */
    int eliminate();
    
    /**
     * 缓存是否已经满
     * @return
     */
    boolean isFull();
    
    /**
     * 清除所有缓存对象
     */
    void clear();
 
    /**
     * 返回缓存大小
     * 
     * @return  
     */
    int getCacheSize();
 
    /**
     * 缓存中是否为空
     */
    boolean isEmpty();
    
    /**
     * 设置缓存大小
     * @param cacheSize
     * @return
     */
    <A,B> Cache<A,B> cacheSize(int cacheSize);
    
    /**
     * 设置缓存有效期
     * @param expire
     * @return
     */
    <A,B> Cache<A,B> expire(long expire);
 }
