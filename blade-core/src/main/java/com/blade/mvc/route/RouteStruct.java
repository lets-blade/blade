package com.blade.mvc.route;

import com.blade.annotation.route.DELETE;
import com.blade.annotation.route.GET;
import com.blade.annotation.route.POST;
import com.blade.annotation.route.PUT;
import com.blade.annotation.route.ANY;
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

    ANY ANY;
    GET GET;
    POST POST;
    PUT PUT;
    DELETE DELETE;
    String      nameSpace;
    String      suffix;
    Class<?>    routeType;
    Object      controller;
    Method      method;

    private static final String[] DEFAULT_PATHS = new String[]{};

    public HttpMethod getMethod() {
        if (null != ANY) {
            return ANY.method();
        }
        if (null != GET) {
            return HttpMethod.GET;
        }
        if (null != POST) {
            return HttpMethod.POST;
        }
        if (null != PUT) {
            return HttpMethod.PUT;
        }
        if (null != DELETE) {
            return HttpMethod.DELETE;
        }
        return HttpMethod.ALL;
    }

    public String[] getPaths() {
        if (null != ANY) {
            return ANY.value();
        }
        if (null != GET) {
            return GET.value();
        }
        if (null != POST) {
            return POST.value();
        }
        if (null != PUT) {
            return PUT.value();
        }
        if (null != DELETE) {
            return DELETE.value();
        }
        return DEFAULT_PATHS;
    }
}
