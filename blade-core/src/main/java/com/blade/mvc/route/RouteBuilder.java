package com.blade.mvc.route;

import com.blade.annotation.Path;
import com.blade.annotation.route.*;
import com.blade.kit.BladeKit;
import com.blade.kit.ReflectKit;
import com.blade.mvc.RouteContext;
import com.blade.mvc.http.HttpMethod;
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

        routeMatcher.addRoute(com.blade.mvc.route.Route.builder()
                .targetType(webHook)
                .action(before)
                .path(pattern)
                .httpMethod(HttpMethod.BEFORE)
                .build());

        routeMatcher.addRoute(com.blade.mvc.route.Route.builder()
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

            ANY ANY = method.getAnnotation(ANY.class);
            GET GET = method.getAnnotation(GET.class);
            POST POST = method.getAnnotation(POST.class);
            PUT PUT = method.getAnnotation(PUT.class);
            DELETE DELETE = method.getAnnotation(DELETE.class);

            this.parseRoute(RouteStruct.builder().ANY(ANY)
                    .GET(GET).POST(POST)
                    .PUT(PUT).DELETE(DELETE)
                    .nameSpace(nameSpace)
                    .suffix(suffix).routeType(routeType)
                    .controller(controller).method(method)
                    .build());
        }
    }

    private void parseRoute(RouteStruct routeStruct) {
        // build multiple route
        HttpMethod methodType = routeStruct.getMethod();
        String[] paths = routeStruct.getPaths();
        if (paths.length > 0) {
            for (String path : paths) {
                String pathV = getRoutePath(path, routeStruct.nameSpace, routeStruct.suffix);

                routeMatcher.addRoute(com.blade.mvc.route.Route.builder()
                        .target(routeStruct.controller)
                        .targetType(routeStruct.routeType)
                        .action(routeStruct.method)
                        .path(pathV)
                        .httpMethod(methodType)
                        .build());
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

}