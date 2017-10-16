package com.blade.mvc.hook;

/**
 * Request WebHook
 *
 * @author biezhi
 * 2017/6/2
 */
@FunctionalInterface
public interface WebHook {

    /**
     * In the route calls before execution
     *
     * @param signature the current route signature
     * @return return true then execute next route, else interrupt the current request
     */
    boolean before(Signature signature);

    /**
     * In the route calls after execution
     *
     * @param signature the current route signature
     * @return return true then execute next route, else interrupt the current request. default is true
     */
    default boolean after(Signature signature) {
        return true;
    }

}
