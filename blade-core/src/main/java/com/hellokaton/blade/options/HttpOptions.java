package com.hellokaton.blade.options;

import com.hellokaton.blade.mvc.http.HttpSession;
import com.hellokaton.blade.mvc.http.Session;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HttpOptions {

    public static final int DEFAULT_MAX_CONTENT_SIZE = 20971520; // 20MB
    public static final int DEFAULT_SESSION_TIMEOUT = 7200; // 2 hour
    public static final String DEFAULT_SESSION_KEY = "SESSION";

    /**
     * Maximum length of the requested content.
     * If the length exceeds this value, a 413 status code is returned
     */
    private int maxContentSize = DEFAULT_MAX_CONTENT_SIZE;

    private boolean enableGzip;
    private boolean enableSession;
    private boolean enableRequestCost;

    private String sessionKey = DEFAULT_SESSION_KEY;
    private Integer sessionTimeout = DEFAULT_SESSION_TIMEOUT;
    private Integer cacheTimeout;

    /**
     * Session implementation type, the default is HttpSession.
     * <p>
     * When you need to be able to achieve similar RedisSession
     */
    private Class<? extends Session> sessionType = HttpSession.class;

    public static HttpOptions create() {
        return new HttpOptions();
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
