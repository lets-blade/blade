package com.blade.mvc.handler;

import com.blade.mvc.RouteContext;

/**
 * Route logic handler
 *
 * @author biezhi
 * 2017/5/31
 */
@FunctionalInterface
public interface RouteHandler {

    /**
     * Route handler
     *
     * @param context the current request context instance
     */
    void handle(RouteContext context);

}