package blade.kit.log;

import java.util.logging.Level;

/**
 * JDK的日志适配
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class JDKLoggerAdaptor extends Logger {
	
	private java.util.logging.Logger logger;
	
	public JDKLoggerAdaptor() {
	}
	
	public JDKLoggerAdaptor(String name) {
		this.name = name;
		logger = java.util.logging.Logger.getLogger(name);
	}
	
	private Level getLevel(int level) {
		switch (level) {
		case TRACE:
			return Level.FINEST;
		case DEBUG:
			return Level.FINE;
		case INFO:
			return Level.INFO;
		case WARN:
			return Level.WARNING;
		case ERROR:
		case FATAL:
			return Level.SEVERE;
		}
		return Level.INFO;
	}
	
	@Override
	public void log(int level, Object message, Object... args) {
		log(level, message, null, args);
	}

	@Override
	public void log(int level, Object message, Throwable t, Object... args) {
		
		log(getLevel(level), format(message, args), t);
		
	}
	
	private void log(Level level, String msg, Throwable ex) {
		if (logger.isLoggable(level)) {
			// Hack (?) to get the stack trace.
			Throwable dummyException = new Throwable();
			StackTraceElement locations[] = dummyException.getStackTrace();
			// Caller will be the third element
			String cname = "unknown";
			String method = "unknown";
			if (locations != null && locations.length > 3) {
				StackTraceElement caller = locations[3];
				cname = caller.getClassName();
				method = caller.getMethodName();
			}

			if (ex == null) {
				logger.logp(level, cname, method, msg);
			} else {
				logger.logp(level, cname, method, msg, ex);
			}
		}

	}

}
