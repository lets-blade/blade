package com.blade.mvc.route;

import com.blade.kit.BladeKit;
import com.blade.kit.ReflectKit;
import com.blade.mvc.annotation.*;
import com.blade.mvc.hook.Signature;
import com.blade.mvc.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * Route builder
 *
 * @author <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.5
 */
public class RouteBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(RouteBuilder.class);

    private RouteMatcher routeMatcher;

    public RouteBuilder(RouteMatcher routeMatcher) {
        this.routeMatcher = routeMatcher;
    }

    public void addWebHook(final Class<?> webHook, Object hook) {
        Path path = webHook.getAnnotation(Path.class);
        String pattern = "/.*";
        if (null != path) {
            pattern = path.value();
        }

        Method before = ReflectKit.getMethod(webHook, "before", Signature.class);
        Method after = ReflectKit.getMethod(webHook, "after", Signature.class);
        buildRoute(webHook, hook, before, pattern, HttpMethod.BEFORE);
        buildRoute(webHook, hook, after, pattern, HttpMethod.AFTER);
    }

    /**
     * Parse all routing in a controller
     *
     * @param router resolve the routing class
     */
    public void addRouter(final Class<?> router, Object controller) {

        Method[] methods = router.getMethods();
        if (BladeKit.isEmpty(methods)) {
            return;
        }

        String nameSpace = null, suffix = null;

        if (null != router.getAnnotation(Path.class)) {
            nameSpace = router.getAnnotation(Path.class).value();
            suffix = router.getAnnotation(Path.class).suffix();
        }

        if (null == nameSpace) {
            LOGGER.warn("Route [{}] not controller annotation", router.getName());
            return;
        }

        for (Method method : methods) {
            com.blade.mvc.annotation.Route mapping = method.getAnnotation(com.blade.mvc.annotation.Route.class);
            GetRoute getRoute = method.getAnnotation(GetRoute.class);
            PostRoute postRoute = method.getAnnotation(PostRoute.class);
            PutRoute putRoute = method.getAnnotation(PutRoute.class);
            DeleteRoute deleteRoute = method.getAnnotation(DeleteRoute.class);

            this.parseRoute(mapping, nameSpace, suffix, router, controller, method);
            this.parseGetRoute(getRoute, nameSpace, suffix, router, controller, method);
            this.parsePostRoute(postRoute, nameSpace, suffix, router, controller, method);
            this.parsePutRoute(putRoute, nameSpace, suffix, router, controller, method);
            this.parseDeleteRoute(deleteRoute, nameSpace, suffix, router, controller, method);

        }
    }

    private void parseRoute(com.blade.mvc.annotation.Route mapping, String nameSpace, String suffix, Class<?> router,
                            Object controller, Method method) {
        //route method
        if (null != mapping) {
            // build multiple route
            HttpMethod methodType = mapping.method();
            String[] paths = mapping.value();
            if (paths.length > 0) {
                for (String path : paths) {
                    String pathV = getRoutePath(path, nameSpace, suffix);
                    this.buildRoute(router, controller, method, pathV, methodType);
                }
            }
        }
    }

    private void parseGetRoute(GetRoute getRoute, String nameSpace, String suffix, Class<?> router,
                               Object controller, Method method) {
        //route method
        if (null != getRoute) {
            // build multiple route
            String[] paths = getRoute.value();
            if (paths.length > 0) {
                for (String path : paths) {
                    String pathV = getRoutePath(path, nameSpace, suffix);
                    this.buildRoute(router, controller, method, pathV, HttpMethod.GET);
                }
            }
        }
    }

    private void parsePostRoute(PostRoute postRoute, String nameSpace, String suffix, Class<?> router,
                                Object controller, Method method) {
        //route method
        if (null != postRoute) {
            // build multiple route
            String[] paths = postRoute.value();
            if (paths.length > 0) {
                for (String path : paths) {
                    String pathV = getRoutePath(path, nameSpace, suffix);
                    this.buildRoute(router, controller, method, pathV, HttpMethod.POST);
                }
            }
        }
    }

    private void parsePutRoute(PutRoute putRoute, String nameSpace, String suffix, Class<?> router,
                               Object controller, Method method) {
        //route method
        if (null != putRoute) {
            // build multiple route
            String[] paths = putRoute.value();
            if (paths.length > 0) {
                for (String path : paths) {
                    String pathV = getRoutePath(path, nameSpace, suffix);
                    this.buildRoute(router, controller, method, pathV, HttpMethod.PUT);
                }
            }
        }
    }

    private void parseDeleteRoute(DeleteRoute deleteRoute, String nameSpace, String suffix, Class<?> router,
                                  Object controller, Method method) {
        //route method
        if (null != deleteRoute) {
            // build multiple route
            String[] paths = deleteRoute.value();
            if (paths.length > 0) {
                for (String path : paths) {
                    String pathV = getRoutePath(path, nameSpace, suffix);
                    this.buildRoute(router, controller, method, pathV, HttpMethod.DELETE);
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
     * @param clazz      route target execution class
     * @param execMethod route execution method
     * @param path       route path
     * @param method     route httpmethod
     */
    private void buildRoute(Class<?> clazz, Object controller, Method execMethod, String path, HttpMethod method) {
        routeMatcher.addRoute(method, path, controller, clazz, execMethod);
    }

}