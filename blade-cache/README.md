# blade-cache

一个简单的缓存引擎，内部有基于`FIFO`，`LFU`，`LRU`算法的基本实现。

## 使用
```java
public class CacheTest {

	@Test
	public void testLRU2(){
		CacheManager cm = CacheManager.getInstance();
		
		Cache<String, Object> cache = cm.newLRUCache();
		cache.set("name:1", "jack");
		cache.set("name:2", "jack2");
		
		System.out.println(cache.get("name:2"));
		
	}
	
	@Test
	public void testAutoClean(){
		CacheManager cm = CacheManager.getInstance();
		cm.setCleanInterval(1000);
		
		Cache<String, Object> cache = cm.newLRUCache();
		cache.set("name:1", "jack");
		cache.set("name:2", "jack2");
		
		System.out.println(cache.get("name:2"));
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(cache.get("name:2"));
	}
	
	@Test
	public void testHashCache(){
		CacheManager cm = CacheManager.getInstance();
		
		Cache<String, Object> cache = cm.newLRUCache();
		cache.hset("user:list", "a1", "123");
		cache.hset("user:list", "a2", "456");
		cache.hset("user:list", "a3", "789");
		
		System.out.println(cache.hget("user:list", "a1"));
		System.out.println(cache.hget("user:list", "a2"));
		System.out.println(cache.hget("user:list", "a3"));
		
	}
	
}
```