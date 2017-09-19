package com.blade.mvc.route;

import com.blade.mvc.annotation.*;
import com.blade.mvc.annotation.Route;
import com.blade.mvc.http.HttpMethod;
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

    Route       mapping;
    GetRoute    getRoute;
    PostRoute   postRoute;
    PutRoute    putRoute;
    DeleteRoute deleteRoute;
    String      nameSpace;
    String      suffix;
    Class<?>    routeType;
    Object      controller;
    Method      method;

    private static final String[] DEFAULT_PATHS = new String[]{};

    public HttpMethod getMethod() {
        if (null != mapping) {
            return mapping.method();
        }
        if (null != getRoute) {
            return HttpMethod.GET;
        }
        if (null != postRoute) {
            return HttpMethod.POST;
        }
        if (null != putRoute) {
            return HttpMethod.PUT;
        }
        if (null != deleteRoute) {
            return HttpMethod.DELETE;
        }
        return HttpMethod.ALL;
    }

    public String[] getPaths() {
        if (null != mapping) {
            return mapping.value();
        }
        if (null != getRoute) {
            return getRoute.value();
        }
        if (null != postRoute) {
            return postRoute.value();
        }
        if (null != putRoute) {
            return putRoute.value();
        }
        if (null != deleteRoute) {
            return deleteRoute.value();
        }
        return DEFAULT_PATHS;
    }
}
