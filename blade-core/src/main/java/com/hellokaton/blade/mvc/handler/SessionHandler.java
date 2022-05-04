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
import com.hellokaton.blade.Environment;
import com.hellokaton.blade.exception.InternalErrorException;
import com.hellokaton.blade.kit.ReflectKit;
import com.hellokaton.blade.kit.StringKit;
import com.hellokaton.blade.kit.UUID;
import com.hellokaton.blade.mvc.http.Request;
import com.hellokaton.blade.mvc.http.Session;
import com.hellokaton.blade.mvc.http.session.SessionManager;
import com.hellokaton.blade.options.HttpOptions;

import java.time.Instant;

import static com.hellokaton.blade.mvc.BladeConst.ENV_KEY_SESSION_KEY;
import static com.hellokaton.blade.mvc.BladeConst.ENV_KEY_SESSION_TIMEOUT;

/**
 * session handler
 *
 * @author biezhi
 * 2017/6/3
 */
public class SessionHandler {

    private final Class<? extends Session> sessionType;
    private final SessionManager sessionManager;
    private int timeout;
    private String sessionKey;

    public SessionHandler(Blade blade) {
        this.sessionType = blade.httpOptions().getSessionType();
        this.sessionManager = blade.sessionManager();

        this.initOptions(blade.httpOptions(), blade.environment());
    }

    public Session createSession(Request request) {
        Session session = getSession(request);

        long now = Instant.now().getEpochSecond();
        if (null == session) {
            long expired = now + timeout;
            session = ReflectKit.newInstance(sessionType);
            if (null == session) {
                throw new InternalErrorException("Unable to create session object :(");
            }
            session.id(UUID.UU32());
            session.created(now);
            session.expired(expired);
            sessionManager.createSession(session);
            return session;
        } else {
            if (session.expired() < now) {
                sessionManager.destroySession(session);
            } else {
                // renewal
                long expired = now + timeout;
                session.expired(expired);
            }
        }
        return session;
    }

    private Session getSession(Request request) {
        String cookieHeader = request.cookie(this.sessionKey);
        if (StringKit.isEmpty(cookieHeader)) {
            return null;
        }
        return sessionManager.getSession(cookieHeader);
    }

    private void initOptions(HttpOptions httpOptions, Environment environment) {
        this.timeout = httpOptions.getSessionTimeout();
        this.sessionKey = httpOptions.getSessionKey();
        if (this.timeout != HttpOptions.DEFAULT_SESSION_TIMEOUT) {
            environment.set(ENV_KEY_SESSION_TIMEOUT, this.timeout);
        } else {
            this.timeout = environment.getInt(ENV_KEY_SESSION_TIMEOUT, HttpOptions.DEFAULT_SESSION_TIMEOUT);
            httpOptions.setSessionTimeout(this.timeout);
        }

        if (!HttpOptions.DEFAULT_SESSION_KEY.equals(this.sessionKey)) {
            environment.set(ENV_KEY_SESSION_KEY, this.sessionKey);
        } else {
            this.sessionKey = environment.get(ENV_KEY_SESSION_KEY, HttpOptions.DEFAULT_SESSION_KEY);
            httpOptions.setSessionKey(this.sessionKey);
        }
    }

}
