package org.sql2o.logging;

/**
 * Created by lars on 2/9/14.
 */
public class Slf4jLogger implements Logger {

    private final org.slf4j.Logger slf4jLogger;

    public Slf4jLogger(org.slf4j.Logger slf4jLogger) {
        this.slf4jLogger = slf4jLogger;
    }

    public void debug(String format, Object[] argArray) {
        slf4jLogger.debug(format, argArray);
    }

    public void debug(String format, Object arg) {
        slf4jLogger.debug(format, arg);
    }

    public void warn(String format) {
        slf4jLogger.warn(format);
    }

    public void warn(String format, Throwable exception) {
        slf4jLogger.warn(format, exception);
    }
}
