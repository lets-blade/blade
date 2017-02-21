/**
 * Copyright (c) 2015, biezhi 王爵 (biezhi.me@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blade.mvc.route;

import com.blade.kit.reflect.ReflectKit;
import com.blade.mvc.annotation.Controller;
import com.blade.mvc.annotation.Intercept;
import com.blade.mvc.annotation.RestController;
import com.blade.mvc.annotation.Route;
import com.blade.mvc.http.HttpMethod;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;
import com.blade.mvc.interceptor.Interceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * Route builder
 *
 * @author    <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.5
 */
public class RouteBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(RouteBuilder.class);

    private Routers routers;

    public RouteBuilder(Routers routers) {
        this.routers = routers;
    }

    /**
     * Parse Interceptor
     *
     * @param interceptor    resolve the interceptor class
     */
    public void addInterceptor(final Class<?> interceptor) {

        boolean hasInterface = ReflectKit.hasInterface(interceptor, Interceptor.class);
        if (null == interceptor || !hasInterface) {
            return;
        }

        Intercept intercept = interceptor.getAnnotation(Intercept.class);
        String partten = "/.*";
        if (null != intercept) {
            partten = intercept.value();
        }

        try {
            Method before = interceptor.getMethod("before", Request.class, Response.class);
            Method after = interceptor.getMethod("after", Request.class, Response.class);
            buildInterceptor(partten, interceptor, before, HttpMethod.BEFORE);
            buildInterceptor(partten, interceptor, after, HttpMethod.AFTER);
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }

    /**
     * Parse all routing in a controller
     *
     * @param router    resolve the routing class
     */
    public void addRouter(final Class<?> router) {

        Method[] methods = router.getMethods();
        if (null == methods || methods.length == 0) {
            return;
        }
        String nameSpace = null, suffix = null;

        if (null != router.getAnnotation(Controller.class)) {
            nameSpace = router.getAnnotation(Controller.class).value();
            suffix = router.getAnnotation(Controller.class).suffix();
        }

        if (null != router.getAnnotation(RestController.class)) {
            nameSpace = router.getAnnotation(RestController.class).value();
            suffix = router.getAnnotation(RestController.class).suffix();
        }

        if (null == nameSpace) {
            LOGGER.warn("Route [{}] not controller annotation", router.getName());
            return;
        }
        for (Method method : methods) {
            Route mapping = method.getAnnotation(Route.class);
            //route method
            if (null != mapping) {
                // build multiple route
                HttpMethod methodType = mapping.method();
                String[] paths = mapping.value();
                if (paths.length > 0) {
                    for (String path : paths) {
                        String pathV = getRoutePath(path, nameSpace, suffix);
                        this.buildRoute(router, method, pathV, methodType);
                    }
                }
            }
        }
    }

    private String getRoutePath(String value, String nameSpace, String suffix) {
        String path = value.startsWith("/") ? value : "/" + value;

        nameSpace = nameSpace.startsWith("/") ? nameSpace : "/" + nameSpace;
        path = nameSpace + path;

        path = path.replaceAll("[/]+", "/");

        path = path.length() > 1 && path.endsWith("/") ? path.substring(0, path.length() - 1) : path;

        path = path + suffix;

        return path;
    }

    /**
     * Build a route
     *
     * @param clazz        route target execution class
     * @param execMethod    route execution method
     * @param path            route path
     * @param method        route httpmethod
     */
    private void buildRoute(Class<?> clazz, Method execMethod, String path, HttpMethod method) {
        routers.buildRoute(path, clazz, execMethod, method);
    }

    /**
     * Build a route
     *
     * @param path            route path
     * @param clazz        route target execution class
     * @param execMethod    route execution method
     * @param method        route httpmethod
     */
    private void buildInterceptor(String path, Class<?> clazz, Method execMethod, HttpMethod method) {
        routers.buildRoute(path, clazz, execMethod, method);
    }

}