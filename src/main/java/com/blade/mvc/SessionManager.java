/**
 * Copyright (c) 2017, biezhi 王爵 (biezhi.me@gmail.com)
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
