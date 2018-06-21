package com.blade.mvc.handler;

import java.io.IOException;

/**
 * Exception Handler interface
 *
 * @author biezhi
 * @date 2017/9/18
 */
@FunctionalInterface
public interface ExceptionHandler {

    String VARIABLE_STACKTRACE = "stackTrace";

    /**
     * Handler exception
     *
     * @param e current request exception
     */
    void handle(Exception e);

    static boolean isResetByPeer(Throwable e) {
        if ("Connection reset by peer".equals(e.getMessage())) {
            return true;
        }
        return false;
    }

}
