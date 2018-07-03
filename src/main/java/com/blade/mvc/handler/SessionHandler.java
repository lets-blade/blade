package com.blade.mvc.handler;

import com.blade.Blade;
import com.blade.kit.ReflectKit;
import com.blade.kit.UUID;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Session;
import com.blade.mvc.http.session.SessionManager;
import com.blade.server.netty.HttpConst;

import java.time.Instant;

import static com.blade.mvc.Const.ENV_KEY_SESSION_KEY;
import static com.blade.mvc.Const.ENV_KEY_SESSION_TIMEOUT;

/**
 * session handler
 *
 * @author biezhi
 * 2017/6/3
 */
public class SessionHandler {

    private final Blade          blade;
    private final SessionManager sessionManager;
    private final String         sessionKey;
    private final int            timeout;

    public SessionHandler(Blade blade) {
        this.blade = blade;
        this.sessionManager = blade.sessionManager();
        this.sessionKey = blade.environment().get(ENV_KEY_SESSION_KEY, HttpConst.DEFAULT_SESSION_KEY);
        this.timeout = blade.environment().getInt(ENV_KEY_SESSION_TIMEOUT, 1800);
    }

    public Session createSession(Request request) {
        Session session = getSession(request);
        if (null == session) {
            long now     = Instant.now().getEpochSecond();
            long expired = now + timeout;

            String sessionId = UUID.UU32();

            session = ReflectKit.newInstance(blade.sessionType());
            session.id(sessionId);
            session.created(now);
            session.expired(expired);
            sessionManager.addSession(session);
            return session;
        } else {
            long now = Instant.now().getEpochSecond();
            if (session.expired() < now) {
                sessionManager.remove(session);
            } else {
                // renewal
                long expired = now + timeout;
                session.expired(expired);
            }
        }
        return session;
    }

    private Session getSession(Request request) {
        String cookieHeader = request.cookie(sessionKey);
        if (null == cookieHeader) {
            return null;
        }
        return sessionManager.getSession(cookieHeader);
    }

}
