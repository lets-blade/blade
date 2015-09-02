package org.sql2o.tools;

import blade.kit.ReflectKit;

/**
 * Detects whether optional sql2o features are available.
 *
 * @author Alden Quimby
 */
public final class FeatureDetector {

    private FeatureDetector()
    {}

    static {
        setCacheUnderscoreToCamelcaseEnabled(true); // enabled by default
    }

    private static Boolean jodaTimeAvailable;
    private static Boolean oracleAvailable;
    private static boolean cacheUnderscoreToCamelcaseEnabled;

    /**
     * @return {@code true} if Joda-Time is available, {@code false} otherwise.
     */
    public static boolean isJodaTimeAvailable() {
        if (jodaTimeAvailable == null) {
            jodaTimeAvailable = ReflectKit.isPresent("org.joda.time.DateTime");
        }
        return jodaTimeAvailable;
    }

    /**
     * @return {@code true} if oracle.sql is available, {@code false} otherwise.
     */
    public static boolean isOracleAvailable() {
        if (oracleAvailable == null) {
            oracleAvailable = ReflectKit.isPresent("oracle.sql.TIMESTAMP");
        }
        return oracleAvailable;
    }

    /**
     *
     * @return {@code true} if caching of underscore to camelcase is enabled.
     */
    public static boolean isCacheUnderscoreToCamelcaseEnabled() {
        return cacheUnderscoreToCamelcaseEnabled;
    }

    /**
     * Turn caching of underscore to camelcase on or off.
     */
    public static void setCacheUnderscoreToCamelcaseEnabled(boolean cacheUnderscoreToCamelcaseEnabled) {
        FeatureDetector.cacheUnderscoreToCamelcaseEnabled = cacheUnderscoreToCamelcaseEnabled;
    }
}