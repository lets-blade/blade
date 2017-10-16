package com.blade.mvc.http;

import java.util.Map;

/**
 * Session
 *
 * @author <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.5
 */
public interface Session {

    /**
     * Get current session id
     *
     * @return return session id
     */
    String id();

    /**
     * Get current session attribute by name
     *
     * @param name attribute name
     * @param <T>  Class Type
     * @return return attribute
     */
    <T> T attribute(String name);

    /**
     * Set current session attribute
     *
     * @param name  attribute name
     * @param value attribute value
     */
    void attribute(String name, Object value);

    /**
     * Get current session attributes
     *
     * @return return attributes
     */
    Map<String, Object> attributes();

    /**
     * Remove current session attribute
     *
     * @param name
     */
    default void remove(String name) {
        removeAttribute(name);
    }

    /**
     * Remove current session attribute
     *
     * @param name
     */
    void removeAttribute(String name);

    /**
     * Get current session create unix time.
     *
     * @return return created time
     */
    long created();

    /**
     * Get current session expired unix time.
     *
     * @return return expired time
     */
    long expired();

}