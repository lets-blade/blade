package org.sql2o.tools;

/**
 * @author Alden Quimby
 */
public final class ClassUtils {

//    private static ClassLoader getClassLoader() {
//        return Thread.currentThread().getContextClassLoader();
//    }

    /**
     * Check whether the {@link Class} identified by the supplied name is present.
     *
     * @param className the name of the class to check
     * @return true if class is present, false otherwise
     */
    public static boolean isPresent(String className) {
        try {
            // what's wrong with old plain Class.forName
            // this code supposed to work everywhere including containers
            Class.forName(className);
            // getClassLoader().loadClass(className);
            return true;
        }
        catch (Throwable ex) {
            return false;
        }
    }
}
