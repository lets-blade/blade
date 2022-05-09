package com.hellokaton.blade.mvc.route;

import com.hellokaton.blade.annotation.Path;
import com.hellokaton.blade.annotation.route.*;
import com.hellokaton.blade.kit.BladeKit;
import com.hellokaton.blade.kit.ReflectKit;
import com.hellokaton.blade.mvc.RouteContext;
import com.hellokaton.blade.mvc.http.HttpMethod;
import com.hellokaton.blade.mvc.ui.ResponseType;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * Route builder
 *
 * @author <a href="mailto:hellokaton@gmail.com" target="_blank">hellokaton</a>
 * @since 1.5
 */
@Slf4j
public class RouteBuilder {

    private final RouteMatcher routeMatcher;

    public RouteBuilder(RouteMatcher routeMatcher) {
        this.routeMatcher = routeMatcher;
    }

    public void addWebHook(final Class<?> webHook, String pattern) {
        Method before = ReflectKit.getMethod(webHook, HttpMethod.BEFORE.name().toLowerCase(), RouteContext.class);
        Method after = ReflectKit.getMethod(webHook, HttpMethod.AFTER.name().toLowerCase(), RouteContext.class);

        routeMatcher.addRoute(Route.builder()
                .targetType(webHook)
                .action(before)
                .path(pattern)
                .httpMethod(HttpMethod.BEFORE)
                .build());

        routeMatcher.addRoute(Route.builder()
                .targetType(webHook)
                .action(after)
                .path(pattern)
                .httpMethod(HttpMethod.AFTER)
                .build());
    }

    /**
     * Parse all routing in a controller
     *
     * @param routeType resolve the routing class,
     *                  e.g RouteHandler.class or some controller class
     */
    public void addRouter(final Class<?> routeType, Object controller) {

        Method[] methods = routeType.getDeclaredMethods();
        if (BladeKit.isEmpty(methods)) {
            return;
        }

        String nameSpace = null, suffix = null;
        if (null != routeType.getAnnotation(Path.class)) {
            nameSpace = routeType.getAnnotation(Path.class).value();
            suffix = routeType.getAnnotation(Path.class).suffix();
        }

        if (null == nameSpace) {
            log.warn("Route [{}] not path annotation", routeType.getName());
            return;
        }

        for (Method method : methods) {

            ANY any = method.getAnnotation(ANY.class);
            GET get = method.getAnnotation(GET.class);
            POST post = method.getAnnotation(POST.class);
            PUT put = method.getAnnotation(PUT.class);
            DELETE delete = method.getAnnotation(DELETE.class);

            this.parseRoute(RouteStruct.builder().any(any)
                    .get(get).post(post)
                    .put(put).delete(delete)
                    .nameSpace(nameSpace)
                    .suffix(suffix).routeType(routeType)
                    .controller(controller).method(method)
                    .build());
        }
    }

    private void parseRoute(RouteStruct routeStruct) {
        // build multiple route
        HttpMethod methodType = routeStruct.getMethod();
        ResponseType responseType = routeStruct.getResponseType();
        String[] paths = routeStruct.getPaths();
        if (paths.length <= 0) {
            return;
        }
        for (String path : paths) {
            String pathV = getRoutePath(path, routeStruct.nameSpace, routeStruct.suffix);

            routeMatcher.addRoute(Route.builder()
                    .target(routeStruct.controller)
                    .targetType(routeStruct.routeType)
                    .action(routeStruct.method)
                    .path(pathV)
                    .httpMethod(methodType)
                    .responseType(responseType)
                    .build());
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

}