package com.blade.mvc.hook;

/**
 * @author biezhi
 *         2017/6/2
 */
@FunctionalInterface
public interface WebHook {

    boolean before(Invoker invoker);

    default boolean after(Invoker invoker) {
        return true;
    }

}
