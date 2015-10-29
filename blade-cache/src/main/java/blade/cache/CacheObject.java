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

/**
 * <p>
 * 缓存实体对象
 * </p>
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class CacheObject<K, V> {

	private K key;
	private V value;
	private long expires; // 对象存活时间(time-to-live)
	private long lastAccess; // 最后访问时间
	private long accessCount; // 访问次数
	private CacheObject<K, V> previous;
	private CacheObject<K, V> next;

	public CacheObject(K k, V v, long expires) {
		this.key = k;
		this.value = v;
		this.expires = expires;
	}

	public CacheObject() {
	}

	/**
	 * @return 返回是否已经过期
	 */
	public boolean isExpired() {
		if (expires == 0) {
			return false;
		}
		return lastAccess + expires < System.currentTimeMillis();
	}

	public V getValue() {
		lastAccess = System.currentTimeMillis();
		accessCount++;
		return value;
	}

	public K getKey() {
		return key;
	}

	public long getExpires() {
		return expires;
	}

	public void setExpires(long expires) {
		this.expires = expires;
	}

	public long getLastAccess() {
		return lastAccess;
	}

	public void setLastAccess(long lastAccess) {
		this.lastAccess = lastAccess;
	}

	public long getAccessCount() {
		return accessCount;
	}

	public void setAccessCount(long accessCount) {
		this.accessCount = accessCount;
	}

	public CacheObject<K, V> getPrevious() {
		return previous;
	}

	public void setPrevious(CacheObject<K, V> previous) {
		this.previous = previous;
	}

	public CacheObject<K, V> getNext() {
		return next;
	}

	public void setNext(CacheObject<K, V> next) {
		this.next = next;
	}

	public void setKey(K key) {
		this.key = key;
	}

	public void setValue(V value) {
		this.value = value;
	}

}