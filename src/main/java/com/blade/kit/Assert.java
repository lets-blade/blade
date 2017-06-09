package com.blade.kit;

import com.blade.BladeException;

/**
 * @author biezhi
 *         2017/5/31
 */
public class Assert {

    public static void greaterThan(double num, double exp, String msg) {
        if (num < exp) {
            throw new BladeException(msg);
        }
    }

    public static void notNull(Object object, String msg) {
        if (null == object) {
            throw new BladeException(msg);
        }
    }

    public static void notEmpty(String str, String msg) {
        if (null == str || "".equals(str)) {
            throw new BladeException(msg);
        }
    }

    public static <T> void notEmpty(T[] arr, String msg) {
        if (null == arr || arr.length == 0) {
            throw new BladeException(msg);
        }
    }

}
