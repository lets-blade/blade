package com.blade.mvc.handler;

import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;

/**
 * @author biezhi
 * @date 2018/6/29
 */
@FunctionalInterface
public interface RequestHandler<T> {

    void handle(T ctx, Request request, Response response) throws Exception;

}
