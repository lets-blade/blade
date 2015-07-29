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
public class SysLoggerAdaptor extends Logger {

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
	
    private static PrintStream outprint = System.out;
    
	public SysLoggerAdaptor() {
	}
	
	public SysLoggerAdaptor(String name) {
		this.name = name;
	}
	
	private String getLevel(int level){
		if(level <= TRACE){
			return "TRACE";
		}
		if(level <= DEBUG){
			return "DEBUG";
		}
		if(level <= INFO){
			return "INFO";
		}
		if(level <= WARN){
			return "WARN";
		}
		if(level <= ERROR){
			outprint = System.err;
			return "ERROR";
		}
		if(level <= FATAL){
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
		}
		
	}

}
