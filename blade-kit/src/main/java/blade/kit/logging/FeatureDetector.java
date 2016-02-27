package blade.kit.logging;

public final class FeatureDetector {

	private FeatureDetector() {
	}

	static {
		setCacheUnderscoreToCamelcaseEnabled(true); // enabled by default
	}

	private static Boolean jodaTimeAvailable;
	private static Boolean slf4jAvailable;
	private static Boolean log4jAvailable;
	private static Boolean oracleAvailable;
	private static boolean cacheUnderscoreToCamelcaseEnabled;

	public static boolean isPresent(String className) {
		try {
			// what's wrong with old plain Class.forName
			// this code supposed to work everywhere including containers
			Class.forName(className);
			// getClassLoader().loadClass(className);
			return true;
		} catch (Throwable ex) {
			return false;
		}
	}
	
	public static boolean isJodaTimeAvailable() {
		if (jodaTimeAvailable == null) {
			jodaTimeAvailable = isPresent("org.joda.time.DateTime");
		}
		return jodaTimeAvailable;
	}

	public static boolean isSlf4jAvailable() {
		if (slf4jAvailable == null) {
			slf4jAvailable = isPresent("org.slf4j.Logger");
		}
		return slf4jAvailable;
	}
	
	public static boolean isLog4jAvailable() {
		if (log4jAvailable == null) {
			log4jAvailable = isPresent("org.apache.log4j.Logger");
		}
		return log4jAvailable;
	}

	public static boolean isOracleAvailable() {
		if (oracleAvailable == null) {
			oracleAvailable = isPresent("oracle.sql.TIMESTAMP");
		}
		return oracleAvailable;
	}

	public static boolean isCacheUnderscoreToCamelcaseEnabled() {
		return cacheUnderscoreToCamelcaseEnabled;
	}

	public static void setCacheUnderscoreToCamelcaseEnabled(boolean cacheUnderscoreToCamelcaseEnabled) {
		FeatureDetector.cacheUnderscoreToCamelcaseEnabled = cacheUnderscoreToCamelcaseEnabled;
	}
}