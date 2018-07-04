package com.blade.mvc.http.session;

import com.blade.mvc.http.Session;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * Session cleaner
 *
 * @author biezhi
 * @date 2018/7/3
 */
@Slf4j
public class SessionCleaner implements Runnable {

    private SessionManager sessionManager;

    public SessionCleaner(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Collection<Session> sessions = sessionManager.sessionMap().values();
                sessions.parallelStream().filter(this::expires).forEach(sessionManager::destorySession);
            } catch (Exception e) {
                log.error("Session clean error", e);
            } finally {
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException e) {
                    log.error("Session cleaner interrupted", e);
                }
            }

        }
    }

    private boolean expires(Session session) {
        long now = Instant.now().getEpochSecond();
        return session.expired() < now;
    }

}
