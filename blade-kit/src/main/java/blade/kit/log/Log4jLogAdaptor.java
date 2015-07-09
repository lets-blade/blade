package blade.kit.log;

import org.apache.log4j.Level;
import org.apache.log4j.Priority;

/**
 * Log4j日志适配
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
@SuppressWarnings("deprecation")
public class Log4jLogAdaptor extends Logger{
	
	private static final String FQCN = Log4jLogAdaptor.class.getName();
	private static Priority traceLevel;
	private org.apache.log4j.Logger logger;
	
	static {
		try {
			traceLevel = (Priority) Level.class.getDeclaredField("TRACE").get(null);
		} catch (Exception ex) {
			traceLevel = Priority.DEBUG;
		}
	}
	
	public Log4jLogAdaptor(String name) {
		this.name = name;
		logger = org.apache.log4j.Logger.getLogger(name);
	}
	
	@Override
	public String getName() {
		return this.name;
	}

	private Priority getLevel(int level) {
		switch (level) {
		case TRACE:
			return traceLevel;
		case DEBUG:
			return Priority.DEBUG;
		case INFO:
			return Priority.INFO;
		case WARN:
			return Priority.WARN;
		case ERROR:
			return Priority.ERROR;
		case FATAL:
			return Priority.FATAL;
		}
		return Priority.DEBUG;
	}
	
	@Override
	public void log(int level, Object message, Object... args) {
		message = format(message, args);
		logger.log(FQCN, getLevel(level), message, null);
	}

	@Override
	public void log(int level, Object message, Throwable t, Object... args) {
		message = format(message, args);
		logger.log(FQCN, getLevel(level), message, t);
	}
	
	@Override
	public void trace(Object message) {
		
	}

	@Override
	public void trace(Object message, Object... args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trace(Object message, Throwable t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trace(Object message, Throwable t, Object... args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void debug(Object message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void debug(Object message, Object... args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void debug(Object message, Throwable t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void debug(Object message, Throwable t, Object... args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void info(Object message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void info(Object message, Object... args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void info(Object message, Throwable t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void info(Object message, Throwable t, Object... args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(Object message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(Object message, Object... args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(Object message, Throwable t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(Object message, Throwable t, Object... args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(Object message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(Object message, Object... args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(Object message, Throwable t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(Object message, Throwable t, Object... args) {
		// TODO Auto-generated method stub
		
	}

}
