package com.blade.mvc.route.mapping.dynamic;

import com.blade.mvc.http.HttpMethod;
import com.blade.mvc.route.Route;
import lombok.Value;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author: dqyuan
 * @date: 2020/06/26
 */
public class TrieMappingTest {

    @Value(staticConstructor = "of")
    private static class MethodAndPath {
        HttpMethod method;
        String path;
    }

    @Value(staticConstructor = "of")
    private static class Pair {
        String key;
        String value;
    }

    private Map<String, String> map(List<Pair> pairs) {
        return pairs.stream()
                .collect(Collectors.toMap(
                   Pair::getKey,
                   Pair::getValue
                ));
    }


    private void addSimpleRoute(TrieMapping trieMapping, HttpMethod httpMethod, String path) {
        trieMapping.addRoute(httpMethod,
                new Route(null, path,
                        null, null, null, null), Collections.emptyList());
    }

    private TrieMapping initMapping(List<MethodAndPath> methodAndPaths) {
        TrieMapping trieMapping = new TrieMapping();
        methodAndPaths.forEach(mp -> addSimpleRoute(trieMapping, mp.getMethod(), mp.getPath()));
        return trieMapping;
    }

    @Test
    public void testStaticFind() {
        TrieMapping trieMapping = initMapping(Arrays.asList(
                MethodAndPath.of(HttpMethod.GET, "/uu/aac"),
                MethodAndPath.of(HttpMethod.GET, "/uu/poi"),
                MethodAndPath.of(HttpMethod.GET, "/pp/cei"),
                MethodAndPath.of(HttpMethod.POST, "/abc/def"))
        );

        assertEquals("/uu/poi",
                trieMapping.findRoute("GET", "/uu/poi").getOriginalPath());
        assertEquals("/uu/aac",
                trieMapping.findRoute("GET", "/uu/aac").getOriginalPath());
        assertEquals("/abc/def",
                trieMapping.findRoute("POST", "/abc/def").getOriginalPath());
        assertNull(trieMapping.findRoute("GET", "/uu/ccc"));
        assertNull(trieMapping.findRoute("POST", "/uu/ccc"));
    }

    @Test
    public void testParamUrl() {
        TrieMapping trieMapping = initMapping(Arrays.asList(
                MethodAndPath.of(HttpMethod.GET, "/aaa/:id/:name"),
                MethodAndPath.of(HttpMethod.ALL, "/pp/name/:ppp"),
                MethodAndPath.of(HttpMethod.GET, "/cc/:name/oq"))
        );

        Route route = trieMapping.findRoute("GET", "/aaa/mm/hh");
        assertEquals("/aaa/:id/:name", route.getOriginalPath());
        assertEquals(map(Arrays.asList(
                Pair.of("id", "mm"),
                Pair.of("name", "hh")
        )), route.getPathParams());

        route = trieMapping.findRoute("GET", "/pp/name/lala");
        assertEquals("/pp/name/:ppp", route.getOriginalPath());
        assertEquals(map(Arrays.asList(
                Pair.of("ppp", "lala")
        )), route.getPathParams());

        route = trieMapping.findRoute("GET", "/cc/ok/oq");
        assertEquals("/cc/:name/oq", route.getOriginalPath());
        assertEquals(map(Arrays.asList(
                Pair.of("name", "ok")
        )), route.getPathParams());

        assertNull(trieMapping.findRoute("GET", "cc/ok"));
        assertNull(trieMapping.findRoute("GET", "/aaa/mm"));
    }

    @Test
    public void testSuffix() {
        TrieMapping trieMapping = initMapping(Arrays.asList(
                MethodAndPath.of(HttpMethod.GET, "/child/*"))
        );

        assertEquals("/child/*",
                trieMapping.findRoute("GET", "/child/pp").getOriginalPath());
        assertEquals("/child/*",
                trieMapping.findRoute("GET", "/child/pp/ooo").getOriginalPath());
    }

    @Test(expected = IllegalStateException.class)
    public void testConflict() {
        initMapping(Arrays.asList(
                MethodAndPath.of(HttpMethod.GET, "/child/*"),
                MethodAndPath.of(HttpMethod.GET, "/child/:pp")
        ));
    }

}