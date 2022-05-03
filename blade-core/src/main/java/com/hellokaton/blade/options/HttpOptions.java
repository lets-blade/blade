package com.hellokaton.blade.options;

import lombok.Getter;

@Getter
public class HttpOptions {

    private static final int DEFAULT_MAX_CONTENT_SIZE = 20971520; // 20MB
    private static final int DEFAULT_SESSION_TIMEOUT = 1800;
    private static final String DEFAULT_SESSION_KEY = "SESSION";

    /**
     * Maximum length of the requested content.
     * If the length exceeds this value, a 413 status code is returned
     */
    private int maxContentSize;

    private boolean enableGzip;
    private boolean enableSession;

    private String sessionKey;
    private Integer sessionTimeout;

    public static HttpOptions create() {
        HttpOptions httpOptions = new HttpOptions();
        httpOptions.maxContentSize = DEFAULT_MAX_CONTENT_SIZE;
        return httpOptions;
    }

    public HttpOptions enableGzip() {
        this.enableGzip = true;
        return this;
    }

    public HttpOptions enableSession() {
        this.enableSession = true;
        return enableSession(DEFAULT_SESSION_KEY);
    }

    public HttpOptions enableSession(String sessionKey) {
        return enableSession(sessionKey, DEFAULT_SESSION_TIMEOUT);
    }

    public HttpOptions enableSession(String sessionKey, int sessionTimeout) {
        this.enableSession = true;
        this.sessionKey = sessionKey;
        this.sessionTimeout = sessionTimeout;
        return this;
    }

}
