package blade.cache.impl;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import blade.cache.AbstractCache;
import blade.cache.CacheObject;
 
/**
 * LRU  实现
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 * @param <K>
 * @param <V>
 */
public class LRUCache<K, V> extends AbstractCache<K, V> {
 
    public LRUCache(int cacheSize) {
         
        super(cacheSize) ;
 
        //linkedHash已经实现LRU算法 是通过双向链表来实现，当某个位置被命中，通过调整链表的指向将该位置调整到头位置，新加入的内容直接放在链表头，如此一来，最近被命中的内容就向链表头移动，需要替换时，链表最后的位置就是最近最少使用的位置
        this._mCache = new LinkedHashMap<K, CacheObject<K, V>>( cacheSize +1 , 1f,true ) {

        	private static final long serialVersionUID = 1L;

			@Override
            protected boolean removeEldestEntry(Map.Entry<K, CacheObject<K, V>> eldest) {
                return LRUCache.this.removeEldestEntry(eldest);
            }
 
        };
    }
    
    private boolean removeEldestEntry(Map.Entry<K, CacheObject<K, V>> eldest) {
        if (cacheSize == 0)
            return false;
        return size() > cacheSize;
    }
 
    /**
     * 只需要实现清除过期对象就可以了,linkedHashMap已经实现LRU
     */
    @Override
    protected int eliminateCache() {
 
        if(!isNeedClearExpiredObject()){ return 0 ;}
         
        Iterator<CacheObject<K, V>> iterator = _mCache.values().iterator();
        int count  = 0 ;
        while(iterator.hasNext()){
            CacheObject<K, V> cacheObject = iterator.next();
             
            if(cacheObject.isExpired() ){
                iterator.remove(); 
                count++ ;
            }
        }
         
        return count;
    }
 
}