/**
 * Copyright (c) 2018, biezhi 王爵 nice (hellokaton@gmail.com)
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
package com.hellokaton.blade.mvc.handler;

import com.hellokaton.blade.Blade;
import com.hellokaton.blade.kit.ReflectKit;
import com.hellokaton.blade.kit.StringKit;
import com.hellokaton.blade.kit.UUID;
import com.hellokaton.blade.mvc.WebContext;
import com.hellokaton.blade.mvc.http.Request;
import com.hellokaton.blade.mvc.http.Session;
import com.hellokaton.blade.mvc.http.session.SessionManager;

import java.time.Instant;

import static com.hellokaton.blade.mvc.BladeConst.ENV_KEY_SESSION_TIMEOUT;

/**
 * session handler
 *
 * @author biezhi
 * 2017/6/3
 */
public class SessionHandler {

    private final Blade          blade;
    private final SessionManager sessionManager;
    private final int            timeout;

    public SessionHandler(Blade blade) {
        this.blade = blade;
        this.sessionManager = blade.sessionManager();
        this.timeout = blade.environment().getInt(ENV_KEY_SESSION_TIMEOUT, 1800);
    }

    public Session createSession(Request request) {
        Session session = getSession(request);

        long now = Instant.now().getEpochSecond();
        if (null == session) {
            long expired = now + timeout;
            session = ReflectKit.newInstance(blade.sessionType());
            session.id(UUID.UU32());
            session.created(now);
            session.expired(expired);
            sessionManager.createSession(session);
            return session;
        } else {
            if (session.expired() < now) {
                sessionManager.destorySession(session);
            } else {
                // renewal
                long expired = now + timeout;
                session.expired(expired);
            }
        }
        return session;
    }

    private Session getSession(Request request) {
        String cookieHeader = request.cookie(WebContext.sessionKey());
        if (StringKit.isEmpty(cookieHeader)) {
            return null;
        }
        return sessionManager.getSession(cookieHeader);
    }

}
