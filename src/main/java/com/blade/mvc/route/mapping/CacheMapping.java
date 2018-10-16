package com.blade.mvc.route.mapping;

import com.blade.mvc.route.Route;

import java.util.HashMap;
import java.util.Map;

/**
 * @author biezhi
 * @date 2018/10/16
 */
public class CacheMapping {

    private Map<String, Map<String, Route>> cache = new HashMap<>(32);

    public boolean hasRoute(String httpMethod, String path) {
        return cache.containsKey(path) && cache.get(path).containsKey(httpMethod);
    }

    public Route getRoute(String httpMethod, String path) {
        return cache.get(path).get(httpMethod);
    }

    public Route cached(Route route) {
        if (null == route) {
            return null;
        }
        String path       = route.getPath();
        String httpMethod = route.getHttpMethod().name();
        if (cache.containsKey(path)) {
            cache.get(path).put(httpMethod, route);
        } else {
            Map<String, Route> map = new HashMap<>();
            map.put(httpMethod, route);
            cache.put(path, map);
        }
        return route;
    }

}
