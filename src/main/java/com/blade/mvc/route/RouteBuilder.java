package com.blade.mvc.route;

import com.blade.kit.BladeKit;
import com.blade.kit.ReflectKit;
import com.blade.mvc.annotation.*;
import com.blade.mvc.hook.Signature;
import com.blade.mvc.http.HttpMethod;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * Route builder
 *
 * @author <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.5
 */
@Slf4j
public class RouteBuilder {

    private RouteMatcher routeMatcher;

    public RouteBuilder(RouteMatcher routeMatcher) {
        this.routeMatcher = routeMatcher;
    }

    public void addWebHook(final Class<?> webHook, Object hook) {
        Path   path    = webHook.getAnnotation(Path.class);
        String pattern = "/.*";
        if (null != path) {
            pattern = path.value();
        }

        Method before = ReflectKit.getMethod(webHook, "before", Signature.class);
        Method after  = ReflectKit.getMethod(webHook, "after", Signature.class);

        routeMatcher.addRoute(com.blade.mvc.route.Route.builder()
                .target(hook)
                .targetType(webHook)
                .action(before)
                .path(pattern)
                .httpMethod(HttpMethod.BEFORE)
                .build());

        routeMatcher.addRoute(com.blade.mvc.route.Route.builder()
                .target(hook)
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

            com.blade.mvc.annotation.Route mapping     = method.getAnnotation(com.blade.mvc.annotation.Route.class);
            GetRoute                       getRoute    = method.getAnnotation(GetRoute.class);
            PostRoute                      postRoute   = method.getAnnotation(PostRoute.class);
            PutRoute                       putRoute    = method.getAnnotation(PutRoute.class);
            DeleteRoute                    deleteRoute = method.getAnnotation(DeleteRoute.class);

            this.parseRoute(RouteStruct.builder().mapping(mapping)
                    .getRoute(getRoute).postRoute(postRoute)
                    .putRoute(putRoute).deleteRoute(deleteRoute)
                    .nameSpace(nameSpace)
                    .suffix(suffix).routeType(routeType)
                    .controller(controller).method(method)
                    .build());
        }
    }

    // register route object to ioc
    public void register() {
        routeMatcher.register();
    }

    private void parseRoute(RouteStruct routeStruct) {
        // build multiple route
        HttpMethod methodType = routeStruct.getMethod();
        String[]   paths      = routeStruct.getPaths();
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