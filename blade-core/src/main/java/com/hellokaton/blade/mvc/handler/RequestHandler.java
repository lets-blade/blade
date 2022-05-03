package com.hellokaton.blade.mvc.handler;

import com.hellokaton.blade.mvc.WebContext;

/**
 * Request Handler
 *
 * @author biezhi
 * @date 2018/6/29
 */
@FunctionalInterface
public interface RequestHandler {

    void handle(WebContext webContext) throws Exception;

}
