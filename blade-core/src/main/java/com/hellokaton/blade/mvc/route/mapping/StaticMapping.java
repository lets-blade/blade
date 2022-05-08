package com.hellokaton.blade.mvc.route.mapping;

import com.hellokaton.blade.kit.CollectionKit;
import com.hellokaton.blade.mvc.http.HttpMethod;
import com.hellokaton.blade.mvc.route.Route;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * Static Route Mapping
 *
 * @author biezhi
 * @date 2018/10/16
 */
@Deprecated
@NoArgsConstructor
public class StaticMapping {

    private Map<String, Map<String, Route>> mapping = new HashMap<>(32);

    public void addRoute(String path, HttpMethod httpMethod, Route route) {
        if (!mapping.containsKey(path)) {
            Map<String, Route> map = CollectionKit.newMap(8);
            map.put(httpMethod.name(), route);
            mapping.put(path, map);
        } else {
            mapping.get(path).put(httpMethod.name(), route);
        }
    }

    public Route findRoute(String path, String httpMethod) {
        if (!mapping.containsKey(path)) {
            return null;
        }
        return mapping.get(path).get(httpMethod);
    }

    public boolean hasPath(String path) {
        return mapping.containsKey(path);
    }

    public void clear() {
        mapping.clear();
    }

}
