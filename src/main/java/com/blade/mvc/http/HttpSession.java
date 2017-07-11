package com.blade.mvc.http;

import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * HttpSession
 *
 * @author biezhi
 *         2017/5/31
 */
public class HttpSession implements Session {

    private Map<String, Object> attributes = new HashMap<>();
    private String              id         = null;
    @Setter
    private String              ip         = null;
    @Setter
    private long                created    = -1;
    @Setter
    private long                expired    = -1;

    public HttpSession(String id) {
        this.id = id;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public <T> T attribute(String name) {
        Object object = this.attributes.get(name);
        return null != object ? (T) object : null;
    }

    @Override
    public void attribute(String name, Object value) {
        this.attributes.put(name, value);
    }

    @Override
    public Map<String, Object> attributes() {
        return attributes;
    }

    @Override
    public void removeAttribute(String name) {
        this.attributes.remove(name);
    }

    @Override
    public long created() {
        return this.created;
    }

    @Override
    public long expired() {
        return this.expired;
    }
}
