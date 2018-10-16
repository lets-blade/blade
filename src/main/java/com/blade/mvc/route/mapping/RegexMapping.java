package com.blade.mvc.route.mapping;

import com.blade.mvc.http.HttpMethod;
import com.blade.mvc.route.PathRegexBuilder;
import com.blade.mvc.route.Route;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static com.blade.kit.BladeKit.isWebHook;

/**
 * Regex Route Mapping
 *
 * @author biezhi
 * @date 2018/10/16
 */
@Slf4j
@NoArgsConstructor
public class RegexMapping {

    private Map<HttpMethod, Map<Integer, FastRouteMappingInfo>> regexRoutes        = new HashMap<>();
    private Map<HttpMethod, Pattern>                            regexRoutePatterns = new HashMap<>();
    private Map<HttpMethod, Integer>                            indexes            = new HashMap<>();
    private Map<HttpMethod, StringBuilder>                      patternBuilders    = new HashMap<>();

    public void addRoute(String path, HttpMethod httpMethod, Route route, List<String> uriVariableNames) {
        if (regexRoutes.get(httpMethod) == null) {
            regexRoutes.put(httpMethod, new HashMap<>());
            patternBuilders.put(httpMethod, new StringBuilder("^"));
            indexes.put(httpMethod, 1);
        }
        int i = indexes.get(httpMethod);
        regexRoutes.get(httpMethod).put(i, new FastRouteMappingInfo(route, uriVariableNames));
        indexes.put(httpMethod, i + uriVariableNames.size() + 1);
        patternBuilders.get(httpMethod).append(new PathRegexBuilder().parsePath(path));
    }

    public FastRouteMappingInfo findMappingInfo(HttpMethod httpMethod, int index) {
        return regexRoutes.get(httpMethod).get(index);
    }

    public void register() {
        patternBuilders.keySet().stream()
                .filter(this::notIsWebHook)
                .forEach(this::registerRoutePatterns);
    }

    private void registerRoutePatterns(HttpMethod httpMethod) {
        StringBuilder patternBuilder = patternBuilders.get(httpMethod);
        if (patternBuilder.length() > 1) {
            patternBuilder.setCharAt(patternBuilder.length() - 1, '$');
        }
        log.debug("Fast Route Method: {}, regex: {}", httpMethod, patternBuilder);
        regexRoutePatterns.put(httpMethod, Pattern.compile(patternBuilder.toString()));
    }

    public Pattern findPattern(HttpMethod requestMethod) {
        return regexRoutePatterns.get(requestMethod);
    }

    private boolean notIsWebHook(HttpMethod httpMethod) {
        return !isWebHook(httpMethod);
    }

    public void clear() {
        regexRoutes.clear();
        regexRoutePatterns.clear();
        indexes.clear();
        patternBuilders.clear();
    }
}
