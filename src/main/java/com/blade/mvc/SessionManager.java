package com.blade.mvc;

import com.blade.mvc.http.Session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SessionManager
 *
 * @author biezhi
 *         2017/6/3
 */
public class SessionManager {

    private Map<String, Session> sessionMap;

    public SessionManager() {
        this.sessionMap = new ConcurrentHashMap<>();
    }

    public Session getSession(String id) {
        return sessionMap.get(id);
    }

    public void addSession(Session session) {
        sessionMap.put(session.id(), session);
    }

    public void clear() {
        sessionMap.clear();
    }

    public void remove(Session session) {
        sessionMap.remove(session.id());
    }
}
