package com.blade.kit.json;

import java.util.LinkedHashMap;

/**
 * @author biezhi
 *         2017/6/6
 */
public class Ason<K, V> extends LinkedHashMap<K, V> {

    public Ason() {
    }

    public Ason(int size) {
        super(size);
    }

    public String getString(String key) {
        return this.get(key).toString();
    }

    @Override
    public String toString() {
        return JsonSerializer.serialize(this);
    }
}
