package blade.kit.log;

import java.lang.reflect.Constructor;

/**
 * 日志输出
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public abstract class Logger {

	private static Class<? extends Logger> logClass = SysLoggerAdaptor.class;
	private static Constructor<?> logConstructor;
	
	public static final int TRACE = 10;
	public static final int DEBUG = 20;
	public static final int INFO = 30;
	public static final int WARN = 40;
	public static final int ERROR = 50;
	public static final int FATAL = 60;
	
	private int level = Logger.DEBUG;
	
	/**
	 * 日志名称
	 */
	protected String name;
	
	public abstract void log(int level, Object message, Throwable t, Object... args);
	
	public abstract void log(int level, Object message, Object... args);
	
	static{
		try {
			Class.forName("org.apache.log4j.Logger");
			logClass = Log4jLogAdaptor.class;
		} catch (Exception e) {
			logClass = SysLoggerAdaptor.class;
		}
	}
	
	public static void setLoggerImpl(Class<? extends Logger> loggerClass){
		logClass = loggerClass;
	}
	
	public static Logger getLogger(String name) {
		try {

			if (logConstructor == null) {
				synchronized (Logger.class) {
					if (logConstructor == null)
						logConstructor = logClass.getConstructor(String.class);
				}
			}
			Logger log = (Logger) logConstructor.newInstance(name);
			return log;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static Logger getLogger(Class<?> clazz) {
		return getLogger(clazz.getName());
	}
	
	public static Logger getLogger() {
		String currentClassName = Thread.currentThread().getStackTrace()[2].getClassName();
		return getLogger(currentClassName);
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setLevel(int level) {
		this.level = level;
	}
	
	public void trace(Object message) {
		if (level <= TRACE)
			log(TRACE, message);
	}

	public void trace(Object message, Object... args) {
		if (level <= TRACE)
			log(TRACE, message, args);
	}

	public void trace(Object message, Throwable t) {
		if (level <= TRACE)
			log(TRACE, message, t);
	}

	
	public void trace(Object message, Throwable t, Object... args) {
		if (level <= TRACE)
			log(TRACE, message, t, args);
	}

	
	public void debug(Object message) {
		if (level <= DEBUG)
			log(DEBUG, message);
	}

	
	public void debug(Object message, Object... args) {
		if (level <= DEBUG)
			log(DEBUG, message, args);
	}

	
	public void debug(Object message, Throwable t) {
		if (level <= DEBUG)
			log(DEBUG, message, t);
	}

	
	public void debug(Object message, Throwable t, Object... args) {
		if (level <= DEBUG)
			log(DEBUG, message, t, args);
	}

	
	public void info(Object message) {
		if (level <= INFO)
			log(INFO, message);
	}

	
	public void info(Object message, Object... args) {
		if (level <= INFO)
			log(INFO, message, args);
	}

	
	public void info(Object message, Throwable t) {
		if (level <= INFO)
			log(INFO, message, t);
	}

	
	public void info(Object message, Throwable t, Object... args) {
		if (level <= INFO)
			log(INFO, message, t, args);
	}

	
	public void warn(Object message) {
		if (level <= WARN)
			log(WARN, message);
	}

	
	public void warn(Object message, Object... args) {
		if (level <= WARN)
			log(WARN, message, args);
	}

	
	public void warn(Object message, Throwable t) {
		if (level <= WARN)
			log(WARN, message, t);
	}

	
	public void warn(Object message, Throwable t, Object... args) {
		if (level <= WARN)
			log(WARN, message, t, args);
	}

	
	public void error(Object message) {
		if (level <= ERROR)
			log(ERROR, message);
	}

	
	public void error(Object message, Object... args) {
		if (level <= ERROR)
			log(ERROR, message, args);
	}

	
	public void error(Object message, Throwable t) {
		if (level <= ERROR)
			log(ERROR, message, t);
	}

	
	public void error(Object message, Throwable t, Object... args) {
		if (level <= ERROR)
			log(ERROR, message, t, args);
	}
	
	
	public void fatal(Object message, Object... args) {
		if (level <= FATAL)
			log(FATAL, message, args);
	}
	
	
	public void fatal(Object message, Throwable t, Object... args) {
		if (level <= FATAL)
			log(FATAL, message, t, args);
	}
	
	protected String format(Object message, Object... args) {
		if (message == null) {
			return null;
		}

		if (args == null || args.length == 0)
			return message.toString();
		else
			return String.format(message.toString(), args);
	}

	public boolean isDebugEnabled() {
		return level <= DEBUG;
	}
}
