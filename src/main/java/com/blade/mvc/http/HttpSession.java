package com.blade.mvc.http;

import java.util.HashMap;
import java.util.Map;

/**
 * @author biezhi
 *         2017/5/31
 */
public class HttpSession implements Session {

    private Map<String, Object> attrs = new HashMap<>();
    private String id;
    private String ip;
    private long created;
    private long expired;

    public HttpSession(String id) {
        this.id = id;
    }

    @Override
    public String id() {
        return id;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public void setExpired(long expired) {
        this.expired = expired;
    }

    @Override
    public <T> T attribute(String name) {
        Object object = this.attrs.get(name);
        return null != object ? (T) object : null;
    }

    @Override
    public void attribute(String name, Object value) {
        this.attrs.put(name, value);
    }

    @Override
    public Map<String, Object> attributes() {
        return attrs;
    }

    @Override
    public void removeAttribute(String name) {
        this.attrs.remove(name);
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
