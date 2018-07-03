package com.blade.mvc.handler;

import com.blade.Blade;
import com.blade.kit.ReflectKit;
import com.blade.kit.UUID;
import com.blade.mvc.WebContext;
import com.blade.mvc.http.Cookie;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;
import com.blade.mvc.http.Session;
import com.blade.mvc.http.session.SessionManager;
import com.blade.server.netty.HttpConst;

import java.time.Instant;
import java.util.Objects;

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
        Session  session  = getSession(request);
        Response response = WebContext.response();
        if (null == session) {
            return createSession(request, Objects.requireNonNull(response));
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

    private Session createSession(Request request, Response response) {
        long now     = Instant.now().getEpochSecond();
        long expired = now + timeout;

        String sessionId = UUID.UU32();
        Cookie cookie    = new Cookie();
        cookie.name(sessionKey);
        cookie.value(sessionId);
        cookie.httpOnly(true);
        cookie.secure(request.isSecure());

        Session session = ReflectKit.newInstance(blade.sessionType());
        session.id(sessionId);
        session.created(now);
        session.expired(expired);
        sessionManager.addSession(session);

        request.cookie(cookie);
        response.cookie(cookie);
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
