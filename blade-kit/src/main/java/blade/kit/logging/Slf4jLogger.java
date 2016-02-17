package blade.kit.logging;

public class Slf4jLogger implements Logger {

    private final org.slf4j.Logger logger;
    
    public Slf4jLogger(org.slf4j.Logger logger) {
        this.logger = logger;
    }
    
    @Override
    public void debug(String msg, Object... args) {
    	logger.debug(msg, args);
    }

    @Override
    public void debug(String msg) {
    	logger.debug(msg);
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
		logger.info(msg, args);
	}

	@Override
	public void error(String msg) {
		logger.error(msg);
	}

	@Override
	public void error(String msg, Object... args) {
		logger.error(msg, args);
	}

	@Override
	public void error(String msg, Throwable t) {
		logger.error(msg, t);
	}

	@Override
	public void warn(String msg, Object... args) {
		logger.warn(msg, args);
	}
}
