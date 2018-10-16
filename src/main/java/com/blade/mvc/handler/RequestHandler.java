package com.blade.mvc.handler;

import com.blade.mvc.WebContext;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;

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
