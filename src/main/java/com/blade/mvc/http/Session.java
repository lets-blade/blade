package com.blade.mvc.http;

import java.util.Map;

/**
 * Session
 *
 * @author <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.5
 */
public interface Session {

    String id();

    <T> T attribute(String name);

    void attribute(String name, Object value);

    Map<String, Object> attributes();

    void removeAttribute(String name);

    long created();

    long expired();

}