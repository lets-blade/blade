package com.blade.mvc.hook;

import com.blade.mvc.RouteContext;

/**
 * Request WebHook
 * <p>
 * Intercept before and after each request logic processing.
 *
 * @author biezhi
 * 2017/6/2
 */
@FunctionalInterface
public interface WebHook {

    /**
     * In the route calls before execution
     *
     * @param context the current route context
     * @return return true then execute next route, else interrupt the current request
     */
    boolean before(RouteContext context);

    /**
     * In the route calls after execution
     *
     * @param context the current route context
     * @return return true then execute next route, else interrupt the current request. default is true
     */
    default boolean after(RouteContext context) {
        return true;
    }

}
