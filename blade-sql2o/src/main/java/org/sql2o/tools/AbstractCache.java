package org.sql2o.tools;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * just inherit and implement evaluate
 * User: dimzon
 * Date: 4/6/14
 * Time: 10:35 PM
 */
public abstract class AbstractCache<K,V,E> {
    private final Map<K,V> map;
    private final Lock rl;
    private final Lock wl;
    /***
     * @param map - allows to define your own map implementation
     */
    public AbstractCache(Map<K, V> map) {
        this.map = map;
        ReadWriteLock rrwl = new ReentrantReadWriteLock();
        rl = rrwl.readLock();
        wl = rrwl.writeLock();
    }

    public AbstractCache(){
        this(new HashMap<K, V>());
    }

    public V get(K key,E param){
        V value;

        try {
            // let's take read lock first
            rl.lock();
            value = map.get(key);
        } finally {
            rl.unlock();
        }
        if(value!=null) return value;

        try {
            wl.lock();
            value = map.get(key);
            if(value==null){
                value = evaluate(key, param);
                map.put(key,value);
            }
        } finally {
            wl.unlock();
        }
        return value;
    }

    protected abstract V evaluate(K key, E param);

}
