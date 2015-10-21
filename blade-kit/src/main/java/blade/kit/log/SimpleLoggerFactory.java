package blade.kit.log;

public class SimpleLoggerFactory implements LoggerFactory {
	
	public Logger getLogger(Class<?> clazz) {
		return new SimpleLogger(clazz.getName());
	}
	
	public Logger getLogger(String name) {
		return new SimpleLogger(name);
	}
}
