/*
 * Copyright (c) 2022, katon (hellokaton@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hellokaton.blade.mvc.http.session;

import com.hellokaton.blade.event.Event;
import com.hellokaton.blade.event.EventManager;
import com.hellokaton.blade.event.EventType;
import com.hellokaton.blade.mvc.http.Session;

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

    private final EventManager eventManager;

    /**
     * Store all Session instances
     */
    private final Map<String, Session> sessionMap;

    /**
     * Create SessionManager
     */
    public SessionManager(EventManager eventManager) {
        this.sessionMap = new ConcurrentHashMap<>(16);
        this.eventManager = eventManager;
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
    public void createSession(Session session) {
        sessionMap.put(session.id(), session);
        Event event = new Event();
        event.attribute("session", session);

        eventManager.fireEvent(EventType.SESSION_CREATED, event);
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
    public void destroySession(Session session) {
        session.attributes().clear();
        sessionMap.remove(session.id());

        Event event = new Event();
        event.attribute("session", session);

        eventManager.fireEvent(EventType.SESSION_DESTROY, event);
    }

    public Map<String, Session> sessionMap() {
        return sessionMap;
    }

}
