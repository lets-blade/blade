package com.hellokaton.blade.mvc.route.mapping.dynamic;

import com.hellokaton.blade.mvc.http.HttpMethod;
import com.hellokaton.blade.mvc.route.DynamicMapping;
import com.hellokaton.blade.mvc.route.PathRegexBuilder;
import com.hellokaton.blade.mvc.route.Route;
import com.hellokaton.blade.mvc.route.mapping.FastRouteMappingInfo;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.hellokaton.blade.kit.BladeKit.isWebHook;

/**
 * Regex Route Mapping
 *
 * @author biezhi
 * @date 2018/10/16
 */
@Slf4j
@NoArgsConstructor
public class RegexMapping implements DynamicMapping {

    private Map<HttpMethod, Map<Integer, FastRouteMappingInfo>> regexRoutes        = new HashMap<>();
    private Map<HttpMethod, Pattern>                            regexRoutePatterns = new HashMap<>();
    private Map<HttpMethod, Integer>                            indexes            = new HashMap<>();
    private Map<HttpMethod, StringBuilder>                      patternBuilders    = new HashMap<>();

    @Override
    public void addRoute(HttpMethod httpMethod, Route route, List<String> uriVariableNames) {
        String path = route.getPath();
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

    @Override
    public void register() {
        patternBuilders.keySet().stream()
                .filter(this::notIsWebHook)
                .forEach(this::registerRoutePatterns);
    }


    @Override
    public Route findRoute(String httpMethod, String path) {
        Map<String, String> uriVariables = new LinkedHashMap<>();

        HttpMethod requestMethod = HttpMethod.valueOf(httpMethod);
        try {
            Pattern pattern = findPattern(requestMethod);
            if (null == pattern) {
                pattern = findPattern(HttpMethod.ALL);
                if (null != pattern) {
                    requestMethod = HttpMethod.ALL;
                }
            }
            if (null == pattern) {
                return null;
            }
            Matcher matcher = null;
            if (path != null) {
                matcher = pattern.matcher(path);
            }
            boolean matched = false;
            if (matcher != null) {
                matched = matcher.matches();
            }
            if (!matched) {
                requestMethod = HttpMethod.ALL;
                pattern = findPattern(requestMethod);
                if (null == pattern) {
                    return null;
                }
                if (path != null) {
                    matcher = pattern.matcher(path);
                }
                matched = matcher != null && matcher.matches();
            }
            Route route = null;
            if (matched) {
                int i;
                for (i = 1; matcher.group(i) == null; i++) ;
                FastRouteMappingInfo mappingInfo = findMappingInfo(requestMethod, i);
                route = new Route(mappingInfo.getRoute());

                // find path variable
                String uriVariable;
                int    j = 0;
                while (++i <= matcher.groupCount() && (uriVariable = matcher.group(i)) != null) {
                    String pathVariable = cleanPathVariable(mappingInfo.getVariableNames().get(j++));
                    uriVariables.put(pathVariable, uriVariable);
                }
                route.setPathParams(uriVariables);
                log.trace("lookup path: " + path + " uri variables: " + uriVariables);
            }
            return route;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void clear() {
        regexRoutes.clear();
        regexRoutePatterns.clear();
        indexes.clear();
        patternBuilders.clear();
    }

    private FastRouteMappingInfo findMappingInfo(HttpMethod httpMethod, int index) {
        return regexRoutes.get(httpMethod).get(index);
    }

    private void registerRoutePatterns(HttpMethod httpMethod) {
        StringBuilder patternBuilder = patternBuilders.get(httpMethod);
        if (patternBuilder.length() > 1) {
            patternBuilder.setCharAt(patternBuilder.length() - 1, '$');
        }
        log.debug("Fast Route Method: {}, regex: {}", httpMethod, patternBuilder);
        regexRoutePatterns.put(httpMethod, Pattern.compile(patternBuilder.toString()));
    }

    private String cleanPathVariable(String pathVariable) {
        if (pathVariable.contains(".")) {
            return pathVariable.substring(0, pathVariable.indexOf('.'));
        }
        return pathVariable;
    }

    private Pattern findPattern(HttpMethod requestMethod) {
        return regexRoutePatterns.get(requestMethod);
    }

    private boolean notIsWebHook(HttpMethod httpMethod) {
        return !isWebHook(httpMethod);
    }

}
