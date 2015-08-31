package org.sql2o.logging;

/**
 * Created by lars on 2/9/14.
 */
public interface Logger {

    public void debug(String format, Object[] argArray);
    public void debug(String format, Object arg);
    public void warn(String format);
    public void warn(String format, Throwable exception);
}
