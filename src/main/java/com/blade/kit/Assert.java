package com.blade.kit;

import java.util.concurrent.Callable;

/**
 * Assert Kit
 *
 * @author biezhi
 *         2017/5/31
 */
public class Assert {

    public static void greaterThan(double num, double exp, String msg) {
        if (num < exp) {
            throw new IllegalArgumentException(msg);
        }
    }

    public static void notNull(Object object, String msg) {
        if (null == object) {
            throw new IllegalArgumentException(msg);
        }
    }

    public static void notEmpty(String str, String msg) {
        if (null == str || "".equals(str)) {
            throw new IllegalArgumentException(msg);
        }
    }

    public static <T> void notEmpty(T[] arr, String msg) {
        if (null == arr || arr.length == 0) {
            throw new IllegalArgumentException(msg);
        }
    }

    public static <T> T wrap(Callable<T> callable) {
        try {
            return callable.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
