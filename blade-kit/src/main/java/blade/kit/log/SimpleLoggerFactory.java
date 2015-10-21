package blade.kit.log;

public class SimpleLoggerFactory implements LoggerFactory {
	
	public Logger getLogger(Class<?> clazz) {
		return new JdkLogger(clazz);
	}
	
	public Logger getLogger(String name) {
		return new JdkLogger(name);
	}
}
