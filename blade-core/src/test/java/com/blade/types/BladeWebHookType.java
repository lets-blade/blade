package com.blade.types;

import com.blade.mvc.RouteContext;
import com.blade.mvc.handler.RouteHandler;

/**
 * @author biezhi
 * @date 2017/9/21
 */
public class BladeWebHookType implements RouteHandler {

    @Override
    public void handle(RouteContext context) {
        System.out.println("before...");
    }

}
