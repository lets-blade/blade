package org.sql2o.logging;

import org.slf4j.LoggerFactory;
import org.sql2o.tools.FeatureDetector;

/**
 * Created by lars on 2/9/14.
 */
public class LocalLoggerFactory {
    // this is thread-safe since static initializer is thread-safe
    private static final boolean slf4jAvailable = FeatureDetector.isSlf4jAvailable();

    public static Logger getLogger(Class clazz) {
        return slf4jAvailable
                ? new Slf4jLogger(LoggerFactory.getLogger(clazz))
                : SysOutLogger.instance;

    }
}
