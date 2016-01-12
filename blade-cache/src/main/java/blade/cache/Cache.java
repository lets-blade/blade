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

import java.util.Set;


/**
 * 
 * <p>
 * Cache top level interface
 * </p>
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public interface Cache<K, V> {
 
	/**
     * Add value object to cache, default cache expire interval
	 * 
	 * @param key
	 * @param value
	 * @throws CacheException
	 */
    void set(K key, V value);
    
    /**
     * Add value object to cache and set expire interval
     * 
     * @param key
     * @param value	过期时间
     * @param expire
     * @throws CacheException
     */
    void set(K key, V value, long expire);
    
    /**
     * Add a hash value to cache
     * @param key
     * @param field
     * @param value
     */
    <F> void hset(K key, F field, V value);
    
    /**
     * Add a hash value to cache and set expire interval
     * @param key
     * @param field
     * @param value
     * @param expire
     */
    <F> void hset(K key, F field, V value, long expire);
    
    /**
     * Search cache object
     * 
     * @param key
     * @return
     * @throws CacheException
     */
    V get(K key);
    
    /**
     * Search cache object
     * 
     * @param key
     * @param field
     * @return
     */
    <F> V hget(K key, F field);
    
    /**
     * Delete cache object
     * 
     * @param key
     */
    void del(K key);
    
    /**
     * Delete cache object
     * 
     * @param key
     */
    void hdel(K key);
    
    /**
     * Delete cache object
     * 
     * @param key
     * @param field
     */
    <F> void del(K key, F field);
    
    /**
     * @return return all keys
     */
    Set<K> keys();
    
    /**
     * @return	return all fields of a key
     */
    <F> Set<F> flieds(K key);
    
    /**
     * @return	return size of current cache
     */
    int size();
    
    /**
     * Eliminate a object
     * 
     * @return  Size of the eliminated object
     */
    int eliminate();
    
    /**
     * Whether cache is full
     * @return
     */
    boolean isFull();
    
    /**
     * Clear all cached objects
     */
    void clear();
 
    /**
     * Return size of cache
     * @return  
     */
    int getCacheSize();
 
    /**
     * Whether cache is Empty
     */
    boolean isEmpty();
    
    /**
     * Set cache size
     * @param cacheSize
     * @return
     */
    <A,B> Cache<A,B> cacheSize(int cacheSize);
    
    /**
     * Set cache expire interval
     * @param expire
     * @return
     */
    <A,B> Cache<A,B> expire(long expire);
 }
