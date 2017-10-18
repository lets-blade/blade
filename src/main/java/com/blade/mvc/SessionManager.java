package com.blade.mvc;

import com.blade.mvc.http.Session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SessionManager
 * <p>
 * This class manages all session instances, including additions and deletions
 *
 * @author biezhi
 * 2017/6/3
 */
public class SessionManager {

    /**
     * Store all Session instances
     */
    private Map<String, Session> sessionMap;

    /**
     * Create SessionManager
     */
    public SessionManager() {
        this.sessionMap = new ConcurrentHashMap<>();
    }

    /**
     * Get a Session instance based on the Session id
     *
     * @param id session id
     * @return Session instance
     */
    public Session getSession(String id) {
        return sessionMap.get(id);
    }

    /**
     * Add a session instance to sessionMap
     *
     * @param session session instance
     */
    public void addSession(Session session) {
        sessionMap.put(session.id(), session);
    }

    /**
     * Clean all session instances
     */
    public void clear() {
        sessionMap.clear();
    }

    /**
     * Remove a session
     *
     * @param session session instance
     */
    public void remove(Session session) {
        sessionMap.remove(session.id());
    }
}
