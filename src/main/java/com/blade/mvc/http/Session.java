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
     * Set session id
     *
     * @param id session id
     */
    void id(String id);

    /**
     * Get current session client ip address
     *
     * @return return request ip address
     */
    String ip();

    /**
     * Set current session client ip address
     *
     * @param ip ip address
     */
    void ip(String ip);

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
     * Set session created unix time
     *
     * @param created created time
     */
    void created(long created);

    /**
     * Get current session expired unix time.
     *
     * @return return expired time
     */
    long expired();

    /**
     * Set session expired unix time
     *
     * @param expired expired time
     */
    void expired(long expired);
}