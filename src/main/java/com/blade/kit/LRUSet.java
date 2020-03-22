package com.blade.kit;

import java.util.*;

/**
 * @author darren
 * @date 2019/10/16 13:39
 */
public class LRUSet<E> extends AbstractSet<E>{
    private transient    HashMap<E, Object> map;
    private static final Object             PRESENT = new Object();

    public LRUSet(int capacity) {
        this.map = new LinkedHashMap<E, Object>(Math.min(32,capacity), .75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry eldest) {
                return size() > capacity;
            }
        };
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return map.get(o) == PRESENT;
    }

    @Override
    public Iterator<E> iterator() {
        return map.keySet().iterator();
    }

    @Override
    public boolean add(E e) {
        return map.put(e, PRESENT) == null;
    }

    @Override
    public boolean remove(Object o) {
        return map.remove(o) == PRESENT;
    }

    @Override
    public void clear() {
        map.clear();
    }
}
