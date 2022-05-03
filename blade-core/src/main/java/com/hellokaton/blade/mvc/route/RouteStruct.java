package com.hellokaton.blade.mvc.route;

import com.hellokaton.blade.annotation.route.*;
import com.hellokaton.blade.mvc.http.HttpMethod;
import com.hellokaton.blade.mvc.ui.ResponseType;
import lombok.Builder;

import java.lang.reflect.Method;

/**
 * Route strut
 *
 * @author biezhi
 * @date 2017/9/19
 */
@Builder
public class RouteStruct {

    ANY any;
    GET get;
    POST post;
    PUT put;
    DELETE delete;
    String nameSpace;
    String suffix;
    Class<?> routeType;
    Object controller;
    Method method;

    private static final String[] DEFAULT_PATHS = new String[]{};

    public HttpMethod getMethod() {
        if (null != any) {
            return any.method();
        }
        if (null != get) {
            return HttpMethod.GET;
        }
        if (null != post) {
            return HttpMethod.POST;
        }
        if (null != put) {
            return HttpMethod.PUT;
        }
        if (null != delete) {
            return HttpMethod.DELETE;
        }
        return HttpMethod.ALL;
    }

    public String[] getPaths() {
        if (null != any) {
            return any.value();
        }
        if (null != get) {
            return get.value();
        }
        if (null != post) {
            return post.value();
        }
        if (null != put) {
            return put.value();
        }
        if (null != delete) {
            return delete.value();
        }
        return DEFAULT_PATHS;
    }

    public ResponseType getResponseType() {
        if (null != any) {
            return any.responseType();
        }
        if (null != get) {
            return get.responseType();
        }
        if (null != post) {
            return post.responseType();
        }
        if (null != put) {
            return put.responseType();
        }
        if (null != delete) {
            return delete.responseType();
        }
        return ResponseType.EMPTY;
    }

}
