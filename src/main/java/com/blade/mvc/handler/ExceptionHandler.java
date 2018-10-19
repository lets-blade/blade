package com.blade.mvc.handler;

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
        if (null != e.getMessage() &&
                e.getMessage().contains("Connection reset by peer")) {
            return true;
        }
        return false;
    }

}
