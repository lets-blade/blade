package com.blade.mvc.hook;

/**
 * Request WebHook
 *
 * @author biezhi
 *         2017/6/2
 */
@FunctionalInterface
public interface WebHook {

    boolean before(Signature signature);

    default boolean after(Signature signature) {
        return true;
    }

}
