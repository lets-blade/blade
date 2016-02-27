package blade.kit.logging;

public class Log4jLogger implements Logger {

    private final org.apache.log4j.Logger logger;
    
    public Log4jLogger(org.apache.log4j.Logger logger) {
        this.logger = logger;
    }
    
    @Override
    public void debug(String msg) {
    	logger.debug(msg);
    }
    
    @Override
    public void debug(String msg, Object... args) {
    }

    public void warn(String msg) {
    	logger.warn(msg);
    }

    public void warn(String msg, Throwable t) {
    	logger.warn(msg, t);
    }

	@Override
	public void info(String msg) {
		logger.info(msg);
	}

	@Override
	public void info(String msg, Object... args) {
	}

	@Override
	public void error(String msg) {
		logger.error(msg);
	}

	@Override
	public void error(String msg, Object... args) {
	}

	@Override
	public void error(String msg, Throwable t) {
		logger.error(msg, t);
	}

	@Override
	public void warn(String msg, Object... args) {
	}
	
}