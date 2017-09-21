package com.blade.types;

import com.blade.mvc.handler.RouteHandler;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;

/**
 * @author biezhi
 * @date 2017/9/21
 */
public class BladeWebHookType implements RouteHandler {

    @Override
    public void handle(Request request, Response response) {
        System.out.println("before...");
    }
}
