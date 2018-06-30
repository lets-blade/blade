package com.blade.mvc.handler;

import com.blade.mvc.RouteContext;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;

/**
 * Route logic handler
 *
 * @author biezhi
 * 2017/5/31
 */
@Deprecated
@FunctionalInterface
public interface RouteHandler0 {

    /**
     * Route handler
     *
     * @param request  current thread Request instance
     * @param response current thread Response instance
     *                 {@link RouteHandler#handle(RouteContext)}
     */
    void handle(Request request, Response response);

}