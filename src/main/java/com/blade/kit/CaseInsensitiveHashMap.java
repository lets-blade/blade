package com.blade.kit;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class CaseInsensitiveHashMap<V> extends LinkedHashMap<String, V> {

    private final Map<String, String> KEY_MAPPING;

    public CaseInsensitiveHashMap() {
        super();
        KEY_MAPPING = new HashMap<>();
    }

    public CaseInsensitiveHashMap(int initialCapacity) {
        super(initialCapacity);
        KEY_MAPPING = new HashMap<>(initialCapacity);
    }

    public CaseInsensitiveHashMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
        this.KEY_MAPPING = new HashMap<>(initialCapacity);
    }

    @Override
    public boolean containsKey(Object key) {
        return super.containsKey(realKey(key));
    }

    @Override
    public V get(Object key) {
        return super.get(realKey(key));
    }

    @Override
    public V put(String key, V value) {
        if (key == null) {
            return super.put(null, value);
        } else {
            String oldKey   = KEY_MAPPING.put(key.toLowerCase(Locale.ENGLISH), key);
            V      oldValue = super.remove(oldKey);
            super.put(key, value);
            return oldValue;
        }
    }

    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        m.forEach(this::put);
    }

    @Override
    public V remove(Object key) {
        Object realKey;
        if (key != null) {
            realKey = KEY_MAPPING.remove(key.toString().toLowerCase(Locale.ENGLISH));
        } else {
            realKey = null;
        }
        return super.remove(realKey);
    }

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        //当 lowerCaseMap 中不包含当前key时直接返回默认值
        if (key != null && !KEY_MAPPING.containsKey(key.toString().toLowerCase(Locale.ENGLISH))) {
            return defaultValue;
        }
        //转换key之后从super中获取值
        return super.getOrDefault(realKey(key), defaultValue);
    }

    @Override
    public void clear() {
        KEY_MAPPING.clear();
        super.clear();
    }

    private Object realKey(Object key) {
        if (key != null) {
            return KEY_MAPPING.get(key.toString().toLowerCase(Locale.ENGLISH));
        } else {
            return null;
        }
    }
}
