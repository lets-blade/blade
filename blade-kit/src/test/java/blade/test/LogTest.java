package blade.test;
import blade.kit.log.Logger;
import blade.kit.log.SysLoggerAdaptor;


public class LogTest {

	public static void main(String[] args) {
		Logger.setLoggerImpl(SysLoggerAdaptor.class);
		Logger logger = Logger.getLogger(LogTest.class);
		logger.info("hello %s", "aaa");
	}
}
