package blade.cache;

/**
 * 缓存实体对象
 *
 * @author <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.0
 * @param <K>
 * @param <V>
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