package blade.kit.logging;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SimpleLogger implements Logger {
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
    
    private static PrintStream outprint = System.out;
    
    private int level = Level.DEBUG;
    
    protected String name;
    
    public SimpleLogger() {
    	String currentClassName = Thread.currentThread().getStackTrace()[2].getClassName();
    	this.name = currentClassName;
    }
    
    public SimpleLogger(Class<?> type) {
    	this.name = type.getName();
    }
    
    private String getLevel(int level){
		if(level <= Level.TRACE){
			return "TRACE";
		}
		if(level <= Level.DEBUG){
			return "DEBUG";
		}
		if(level <= Level.INFO){
			return "INFO";
		}
		if(level <= Level.WARN){
			return "WARN";
		}
		if(level <= Level.ERROR){
			outprint = System.err;
			return "ERROR";
		}
		if(level <= Level.FATAL){
			return "FATAL";
		}
		return "DEBUG";
	}
    
    private String now() {
		return sdf.format(new Date());
	}
	
	public void trace(String message) {
		if (level <= Level.TRACE)
			log(Level.TRACE, message);
	}

	public void trace(String message, Object... args) {
		if (level <= Level.TRACE)
			log(Level.TRACE, message, args);
	}

	public void trace(String message, Throwable t) {
		if (level <= Level.TRACE)
			log(Level.TRACE, message, t);
	}

	
	public void trace(String message, Throwable t, Object... args) {
		if (level <= Level.TRACE)
			log(Level.TRACE, message, t, args);
	}

	
	public void debug(String message) {
		if (level <= Level.DEBUG)
			log(Level.DEBUG, message);
	}

	
	public void debug(String message, Object... args) {
		if (level <= Level.DEBUG)
			log(Level.DEBUG, message, args);
	}

	
	public void debug(String message, Throwable t) {
		if (level <= Level.DEBUG)
			log(Level.DEBUG, message, t);
	}

	
	public void debug(String message, Throwable t, Object... args) {
		if (level <= Level.DEBUG)
			log(Level.DEBUG, message, t, args);
	}

	
	public void info(String message) {
		if (level <= Level.INFO)
			log(Level.INFO, message);
	}

	
	public void info(String message, Object... args) {
		if (level <= Level.INFO)
			log(Level.INFO, message, args);
	}

	
	public void info(String message, Throwable t) {
		if (level <= Level.INFO)
			log(Level.INFO, message, t);
	}

	
	public void info(String message, Throwable t, Object... args) {
		if (level <= Level.INFO)
			log(Level.INFO, message, t, args);
	}

	
	public void warn(String message) {
		if (level <= Level.WARN)
			log(Level.WARN, message);
	}

	
	public void warn(String message, Object... args) {
		if (level <= Level.WARN)
			log(Level.WARN, message, args);
	}

	
	public void warn(String message, Throwable t) {
		if (level <= Level.WARN)
			log(Level.WARN, message, t);
	}

	
	public void warn(String message, Throwable t, Object... args) {
		if (level <= Level.WARN)
			log(Level.WARN, message, t, args);
	}

	
	public void error(String message) {
		if (level <= Level.ERROR)
			log(Level.ERROR, message);
	}

	
	public void error(String message, Object... args) {
		if (level <= Level.ERROR)
			log(Level.ERROR, message, args);
	}

	
	public void error(String message, Throwable t) {
		if (level <= Level.ERROR)
			log(Level.ERROR, message, t);
	}

	
	public void error(String message, Throwable t, Object... args) {
		if (level <= Level.ERROR)
			log(Level.ERROR, message, t, args);
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
	
	public void log(int level, String message, Object... args) {
		log(level, message, null, args);
	}
	
	public void log(int level, String message, Throwable t, Object... args) {
		
		StringBuilder sb = new StringBuilder(now());
		sb.append(" ").append(getLevel(level)).append(" ");
		sb.append("[").append(Thread.currentThread().getName()).append("]").append(" ");
		sb.append(this.name).append(" | ");
		sb.append(format(message, args));
		
		outprint.println(sb.toString());
		if (t != null) {
			t.printStackTrace(System.err);
			System.err.flush();
		}
	}

	protected String format(String message, Object... args) {
		if (message == null) {
			return null;
		}
		if (args == null || args.length == 0){
			if(message.indexOf("{}") != -1){
				message = message.replaceAll("\\{\\}", "");
			}
			return message.toString();
		} else {
			if(message.indexOf("{}") != -1){
				message = message.replaceAll("\\{\\}", "%s");
			}
			return String.format(message.toString(), args);
		}
	}
	
}