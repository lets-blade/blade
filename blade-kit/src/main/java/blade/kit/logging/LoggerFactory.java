package blade.kit.logging;

public class LoggerFactory {
	
    private static final boolean slf4jAvailable = FeatureDetector.isSlf4jAvailable();
    private static final boolean log4jAvailable = FeatureDetector.isLog4jAvailable();
    
    public static Logger getLogger(Class<?> clazz) {
    	if(slf4jAvailable){
    		return new Slf4jLogger(org.slf4j.LoggerFactory.getLogger(clazz));
    	}
    	if(log4jAvailable){
    		return new Log4jLogger(org.apache.log4j.Logger.getLogger(clazz));
    	}
        return new SimpleLogger(clazz);
    }
    
}