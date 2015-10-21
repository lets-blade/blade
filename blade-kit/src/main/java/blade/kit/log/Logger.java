package blade.kit.log;

/**
 * 日志输出
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public abstract class Logger {

	private static LoggerFactory factory;
	
	private int level = Level.DEBUG;
	
	/**
	 * 日志名称
	 */
	protected String name;
	
	public abstract void log(int level, Object message, Throwable t, Object... args);
	
	public abstract void log(int level, Object message, Object... args);
	
	static{
		try {
			Class.forName("org.apache.log4j.Logger");
			factory = new Log4jLoggerFactory();
		} catch (Exception e) {
			factory = new SimpleLoggerFactory();
		}
	}
	
	public static void setLoggerFactory(LoggerFactory loggerFactory){
		factory = loggerFactory;
	}
	
	public static Logger getLogger(String name) {
		return factory.getLogger(name);
	}
	
	public static Logger getLogger(Class<?> clazz) {
		return factory.getLogger(clazz);
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
		if (level <= Level.TRACE)
			log(Level.TRACE, message);
	}

	public void trace(Object message, Object... args) {
		if (level <= Level.TRACE)
			log(Level.TRACE, message, args);
	}

	public void trace(Object message, Throwable t) {
		if (level <= Level.TRACE)
			log(Level.TRACE, message, t);
	}

	
	public void trace(Object message, Throwable t, Object... args) {
		if (level <= Level.TRACE)
			log(Level.TRACE, message, t, args);
	}

	
	public void debug(Object message) {
		if (level <= Level.DEBUG)
			log(Level.DEBUG, message);
	}

	
	public void debug(Object message, Object... args) {
		if (level <= Level.DEBUG)
			log(Level.DEBUG, message, args);
	}

	
	public void debug(Object message, Throwable t) {
		if (level <= Level.DEBUG)
			log(Level.DEBUG, message, t);
	}

	
	public void debug(Object message, Throwable t, Object... args) {
		if (level <= Level.DEBUG)
			log(Level.DEBUG, message, t, args);
	}

	
	public void info(Object message) {
		if (level <= Level.INFO)
			log(Level.INFO, message);
	}

	
	public void info(Object message, Object... args) {
		if (level <= Level.INFO)
			log(Level.INFO, message, args);
	}

	
	public void info(Object message, Throwable t) {
		if (level <= Level.INFO)
			log(Level.INFO, message, t);
	}

	
	public void info(Object message, Throwable t, Object... args) {
		if (level <= Level.INFO)
			log(Level.INFO, message, t, args);
	}

	
	public void warn(Object message) {
		if (level <= Level.WARN)
			log(Level.WARN, message);
	}

	
	public void warn(Object message, Object... args) {
		if (level <= Level.WARN)
			log(Level.WARN, message, args);
	}

	
	public void warn(Object message, Throwable t) {
		if (level <= Level.WARN)
			log(Level.WARN, message, t);
	}

	
	public void warn(Object message, Throwable t, Object... args) {
		if (level <= Level.WARN)
			log(Level.WARN, message, t, args);
	}

	
	public void error(Object message) {
		if (level <= Level.ERROR)
			log(Level.ERROR, message);
	}

	
	public void error(Object message, Object... args) {
		if (level <= Level.ERROR)
			log(Level.ERROR, message, args);
	}

	
	public void error(Object message, Throwable t) {
		if (level <= Level.ERROR)
			log(Level.ERROR, message, t);
	}

	
	public void error(Object message, Throwable t, Object... args) {
		if (level <= Level.ERROR)
			log(Level.ERROR, message, t, args);
	}
	
	
	public void fatal(Object message, Object... args) {
		if (level <= Level.FATAL)
			log(Level.FATAL, message, args);
	}
	
	
	public void fatal(Object message, Throwable t, Object... args) {
		if (level <= Level.FATAL)
			log(Level.FATAL, message, t, args);
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
		return level <= Level.DEBUG;
	}
	
	public boolean isErrorEnabled() {
		return level <= Level.ERROR;
	}
	
	public boolean isInfoEnabled() {
		return level <= Level.INFO;
	}
	
	public boolean isWarnEnabled() {
		return level <= Level.WARN;
	}
	
}
