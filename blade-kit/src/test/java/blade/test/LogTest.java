package blade.test;
import blade.kit.log.Logger;
import blade.kit.log.SysLoggerAdaptor;


public class LogTest {

	public static void main(String[] args) {
		Logger.setLoggerImpl(SysLoggerAdaptor.class);
		Logger logger = Logger.getLogger(LogTest.class);
		//[duxue]2015-07-06 11:02:56,606 INFO [localhost-startStop-1] org.springframework.beans.factory.xml.XmlBeanDefinitionReader.loadBeanDefinitions | Loading XML bean definitions 
		logger.info("hello %s", "aaa");
	}
}
