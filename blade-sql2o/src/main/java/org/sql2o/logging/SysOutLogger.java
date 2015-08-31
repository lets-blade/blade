package org.sql2o.logging;

/**
 * Created by lars on 2/9/14.
 */
public class SysOutLogger implements Logger {

    public static Logger instance = new SysOutLogger();

    //private final Class clazz; // don't used
    private final static long startTime = System.currentTimeMillis();
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static String WARN_LVL = "WARN";
    // private static String ERROR_LVL = "ERROR"; // don't used

    public SysOutLogger() {
    //    this.clazz = clazz;
    }

    public void debug(String format, Object[] argArray) {
        // Don't log debug messages with the SysOutLogger
    }

    public void debug(String format, Object arg) {
        // Don't log debug messages with the SysOutLogger
    }

    public void warn(String format) {
        this.log(format, WARN_LVL, null);
    }

    public void warn(String format, Throwable exception) {
        this.log(format, WARN_LVL, exception);
    }

    private void log(String msg, String level, Throwable exception) {
        StringBuilder buffer = new StringBuilder(); // bit faster

        long millis = System.currentTimeMillis();
        buffer.append(millis - startTime);

        buffer.append(" [");
        buffer.append(Thread.currentThread().getName());
        buffer.append("] ");

        buffer.append(level);
        buffer.append(" ");

        buffer.append(this.getClass().getName());
        buffer.append(" - ");

        buffer.append(msg);

        buffer.append(LINE_SEPARATOR);

        System.err.print(buffer.toString());
        if (exception != null) {
            exception.printStackTrace(System.err);
        }
        System.err.flush();
    }
}
