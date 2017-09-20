package com.blade.kit.json;

import com.blade.kit.StringKit;

import java.util.LinkedHashMap;

/**
 * Ason
 *
 * @author biezhi
 * 2017/6/6
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

    public Integer getInt(String key) {
        String val = getString(key);
        if (StringKit.isNotBlank(val)) {
            return Integer.parseInt(val);
        }
        return null;
    }

    public Long getLong(String key) {
        String val = getString(key);
        if (StringKit.isNotBlank(val)) {
            return Long.parseLong(val);
        }
        return null;
    }

    public Boolean getBoolean(String key) {
        String val = getString(key);
        if (StringKit.isNotBlank(val)) {
            return Boolean.parseBoolean(val);
        }
        return Boolean.FALSE;
    }

    public Double getDouble(String key) {
        String val = getString(key);
        if (StringKit.isNotBlank(val)) {
            return Double.parseDouble(val);
        }
        return null;
    }

    public Float getFloat(String key) {
        String val = getString(key);
        if (StringKit.isNotBlank(val)) {
            return Float.parseFloat(val);
        }
        return null;
    }

    public Short getShort(String key) {
        String val = getString(key);
        if (StringKit.isNotBlank(val)) {
            return Short.parseShort(val);
        }
        return null;
    }

    @Override
    public String toString() {
        return JsonSerializer.serialize(this);
    }
}
