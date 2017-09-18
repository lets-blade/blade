package com.blade.mvc.handler;

import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;

/**
 * route middleware
 *
 * @author biezhi
 *         2017/5/31
 */
@FunctionalInterface
public interface RouteHandler {

    void handle(Request request, Response response);

}