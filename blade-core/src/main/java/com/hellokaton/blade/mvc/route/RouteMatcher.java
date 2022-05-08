package com.hellokaton.blade.mvc.route;

import com.hellokaton.blade.ioc.annotation.Order;
import com.hellokaton.blade.kit.*;
import com.hellokaton.blade.mvc.RouteContext;
import com.hellokaton.blade.mvc.handler.RouteHandler;
import com.hellokaton.blade.mvc.hook.WebHook;
import com.hellokaton.blade.mvc.http.HttpMethod;
import com.hellokaton.blade.mvc.route.mapping.dynamic.TrieMapping;
import com.hellokaton.blade.mvc.ui.ResponseType;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.hellokaton.blade.kit.BladeKit.logAddRoute;

/**
 * Default Route Matcher
 *
 * @author <a href="mailto:hellokaton@gmail.com" target="_blank">hellokaton</a>
 * @since 2.1.2.RELEASE
 */
@Slf4j
public class RouteMatcher {

    private static final Pattern PATH_VARIABLE_PATTERN = Pattern.compile("/(?:([^:/]*):([^/]+))|(\\.\\*)");
    private static final String METHOD_NAME = "handle";

    // Storage URL and route
    private final Map<String, Route> routes = new HashMap<>(16);
    private final Map<String, List<Route>> hooks = new HashMap<>(8);
    private List<Route> middleware = null;
    private final Map<String, Method[]> classMethodPool = new ConcurrentHashMap<>();
    private final Map<Class<?>, Object> controllerPool = new ConcurrentHashMap<>(8);

    private DynamicMapping dynamicMapping = new TrieMapping();

    private Route addRoute(HttpMethod httpMethod, String path, RouteHandler handler) throws NoSuchMethodException {
        Class<?> handleType = handler.getClass();
        Method method = handleType.getMethod(RouteMatcher.METHOD_NAME, RouteContext.class);
        return addRoute(httpMethod, path, handler, RouteHandler.class, method, null);
    }

    Route addRoute(Route route) {
        String path = route.getPath();
        HttpMethod httpMethod = route.getHttpMethod();
        Object controller = route.getTarget();
        Class<?> controllerType = route.getTargetType();
        Method method = route.getAction();
        ResponseType responseType = route.getResponseType();
        return addRoute(httpMethod, path, controller, controllerType, method, responseType);
    }

    private Route addRoute(HttpMethod httpMethod, String path, Object controller,
                           Class<?> controllerType, Method method, ResponseType responseType) {
        String originalPath = path;

        // [/** | /*]
        path = "*".equals(path) ? "/.*" : path;
        path = path.replace("/**", "/.*").replace("/*", "/.*");

        String key = path + "#" + httpMethod.toString();

        // exist
        if (this.routes.containsKey(key)) {
            log.warn("\tRoute {} -> {} has exist", path, httpMethod.toString());
        }

        Route route = new Route(httpMethod, originalPath, path, controller, controllerType, method, responseType);
        if (BladeKit.isWebHook(httpMethod)) {
            Order order = controllerType.getAnnotation(Order.class);
            if (null != order) {
                route.setSort(order.value());
            }
            if (this.hooks.containsKey(key)) {
                this.hooks.get(key).add(route);
            } else {
                List<Route> empty = new ArrayList<>();
                empty.add(route);
                this.hooks.put(key, empty);
            }
        } else {
            this.routes.put(key, route);
        }
        return route;
    }

    public Route addRoute(String path, RouteHandler handler, HttpMethod httpMethod) {
        try {
            return addRoute(httpMethod, path, handler);
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }

    public void route(String path, Class<?> clazz, String methodName) {
        Assert.notNull(methodName, "Method name not is null");
        HttpMethod httpMethod = HttpMethod.ALL;
        if (methodName.contains(":")) {
            String[] methodArr = methodName.split(":");
            httpMethod = HttpMethod.valueOf(methodArr[0].toUpperCase());
            methodName = methodArr[1];
        }
        this.route(path, clazz, methodName, httpMethod);
    }

    public void route(String path, Class<?> clazz, String methodName, HttpMethod httpMethod) {
        try {
            Assert.notNull(path, "Route path not is null.");
            Assert.notNull(clazz, "Route type not is null.");
            Assert.notNull(methodName, "Method name not is null.");
            Assert.notNull(httpMethod, "Request Method not is null.");

            Method[] methods = classMethodPool.computeIfAbsent(clazz.getName(), k -> clazz.getMethods());
            for (Method method : methods) {
                if (method.getName().equals(methodName)) {
                    Object controller = controllerPool.computeIfAbsent(clazz, k -> ReflectKit.newInstance(clazz));
                    addRoute(httpMethod, path, controller, clazz, method, null);
                }
            }
        } catch (Exception e) {
            log.error("Add route method error", e);
        }
    }

    public Route lookupRoute(String httpMethod, String path) {
        return dynamicMapping.findRoute(httpMethod, path);
    }

    private String cleanPathVariable(String pathVariable) {
        if (pathVariable.contains(".")) {
            return pathVariable.substring(0, pathVariable.indexOf('.'));
        }
        return pathVariable;
    }

    public boolean hasBeforeHook() {
        return hooks.values().stream()
                .flatMap(Collection::stream).anyMatch(route -> route.getHttpMethod().equals(HttpMethod.BEFORE));
    }

    public boolean hasAfterHook() {
        return hooks.values().stream()
                .flatMap(Collection::stream).anyMatch(route -> route.getHttpMethod().equals(HttpMethod.AFTER));
    }

    /**
     * Find all in before of the hook
     *
     * @param path request path
     */
    public List<Route> getBefore(String path) {
        String cleanPath = parsePath(path);
        List<Route> collect = hooks.values().stream()
                .flatMap(Collection::stream)
                .sorted(Comparator.comparingInt(Route::getSort))
                .filter(route -> route.getHttpMethod() == HttpMethod.BEFORE && matchesPath(route.getPath(), cleanPath))
                .collect(Collectors.toList());

        this.giveMatch(path, collect);
        return collect;
    }

    /**
     * Find all in after of the hooks
     *
     * @param path request path
     */
    public List<Route> getAfter(String path) {
        String cleanPath = parsePath(path);

        List<Route> afters = hooks.values().stream()
                .flatMap(Collection::stream)
                .sorted(Comparator.comparingInt(Route::getSort))
                .filter(route -> route.getHttpMethod() == HttpMethod.AFTER && matchesPath(route.getPath(), cleanPath))
                .collect(Collectors.toList());

        this.giveMatch(path, afters);
        return afters;
    }

    public List<Route> getMiddleware() {
        return this.middleware;
    }

    public int middlewareCount() {
        if (null == this.middleware) {
            return 0;
        }
        return this.middleware.size();
    }

    /**
     * Sort of path
     *
     * @param uri    request uri
     * @param routes route list
     */
    private void giveMatch(final String uri, List<Route> routes) {
        routes.stream().sorted((o1, o2) -> {
            if (o2.getPath().equals(uri)) {
                return o2.getPath().indexOf(uri);
            }
            return -1;
        });
    }

    /**
     * Matching path
     *
     * @param routePath   route path
     * @param pathToMatch match path
     * @return return match is success
     */
    private boolean matchesPath(String routePath, String pathToMatch) {
        routePath = PathKit.VAR_REGEXP_PATTERN.matcher(routePath).replaceAll(PathKit.VAR_REPLACE);
        return pathToMatch.matches("(?i)" + routePath);
    }

    /**
     * Parse PathKit
     *
     * @param path route path
     * @return return parsed path
     */
    private String parsePath(String path) {
        path = PathKit.fixPath(path);
        try {
            URI uri = new URI(path);
            return uri.getPath();
        } catch (URISyntaxException e) {
            return path;
        }
    }

    /**
     * register route to container
     */
    public void register() {
        routes.values().forEach(route -> logAddRoute(log, route));
        hooks.values().stream().flatMap(Collection::stream).forEach(route -> logAddRoute(log, route));

        Stream.of(routes.values(), hooks.values().stream().findAny().orElse(new ArrayList<>()))
                .flatMap(Collection::stream).forEach(this::registerRoute);

        dynamicMapping.register();
    }

    private void registerRoute(Route route) {
        String path = parsePath(route.getPath());
        Matcher matcher = null;
        if (path != null) {
            matcher = PATH_VARIABLE_PATTERN.matcher(path);
        }
        boolean find = false;
        List<String> uriVariableNames = new ArrayList<>();
        while (matcher != null && matcher.find()) {
            if (!find) {
                find = true;
            }
            String regexName = matcher.group(1);
            String regexValue = matcher.group(2);

            // when /.* is matched, there is no param
            if (regexName == null && regexValue == null) {
                continue;
            }

            // just a simple path param
            if (StringKit.isBlank(regexName)) {
                uriVariableNames.add(regexValue);
            } else {
                //regex path param
                uriVariableNames.add(regexName);
            }
        }
        HttpMethod httpMethod = route.getHttpMethod();
        dynamicMapping.addRoute(httpMethod, route, uriVariableNames);
    }

    public void addMiddleware(WebHook webHook) {
        if (null == this.middleware) {
            this.middleware = new ArrayList<>(8);
        }
        Method method = ReflectKit.getMethod(WebHook.class, "before", RouteContext.class);
        Route route = new Route(HttpMethod.BEFORE, "/*", "/.*", webHook, WebHook.class, method, null);
        this.middleware.add(route);
    }

    public void setDynamicMapping(DynamicMapping dynamicMapping) {
        this.dynamicMapping = dynamicMapping;
    }

}
