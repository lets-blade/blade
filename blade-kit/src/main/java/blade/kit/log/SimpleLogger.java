package blade.kit.log;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Blade自带的日志实现
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class SimpleLogger extends Logger {

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
	
    private static PrintStream outprint = System.out;
    
	public SimpleLogger() {
	}
	
	public SimpleLogger(String name) {
		this.name = name;
	}
	
	private String getLevel(int level){
		if(level <= blade.kit.log.Level.TRACE){
			return "TRACE";
		}
		if(level <= blade.kit.log.Level.DEBUG){
			return "DEBUG";
		}
		if(level <= blade.kit.log.Level.INFO){
			return "INFO";
		}
		if(level <= blade.kit.log.Level.WARN){
			return "WARN";
		}
		if(level <= blade.kit.log.Level.ERROR){
			outprint = System.err;
			return "ERROR";
		}
		if(level <= blade.kit.log.Level.FATAL){
			return "FATAL";
		}
		return "DEBUG";
	}
	
	private String now() {
		return sdf.format(new Date());
	}
	
	@Override
	public void log(int level, Object message, Object... args) {
		log(level, message, null, args);
		
	}

	@Override
	public void log(int level, Object message, Throwable t, Object... args) {
		
		StringBuilder sb = new StringBuilder(now());
		sb.append(" ").append(getLevel(level)).append(" ");
		sb.append("[").append(Thread.currentThread().getName()).append("]").append(" ");
		sb.append(getName()).append(" | ");
		sb.append(format(message, args));
		
		outprint.println(sb.toString());
		if (t != null) {
			t.printStackTrace(System.err);
			System.err.flush();
		}
	}

}
