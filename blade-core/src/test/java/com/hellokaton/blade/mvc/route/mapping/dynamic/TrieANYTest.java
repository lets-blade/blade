package com.hellokaton.blade.mvc.route.mapping.dynamic;

import com.hellokaton.blade.mvc.http.HttpMethod;
import com.hellokaton.blade.mvc.route.Route;
import lombok.Value;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * @author: dqyuan
 * @date: 2020/06/26
 */
public class TrieANYTest {

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
                new Route(httpMethod, path, null, null, null, null), Collections.emptyList());
    }

    @Test
    public void testPartIter() {
        TrieMapping trieMapping = new TrieMapping();
        Iterator<String> partIter = trieMapping.partIter("/ef/sgsg/rgrg");
        Iterator<String> expected = Arrays.asList("ef", "sgsg", "rgrg").iterator();
        while (expected.hasNext()) {
            assertTrue(partIter.hasNext());
            assertEquals(expected.next(), partIter.next());
        }

        Iterator<String> partIter1 = trieMapping.partIter("/ooo/p");
        Iterator<String> expected1 = Arrays.asList("ooo", "p").iterator();
        while (expected1.hasNext()) {
            assertTrue(partIter1.hasNext());
            assertEquals(expected1.next(), partIter1.next());
        }


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
                trieMapping.findRoute("GET", "/uu/poi").getPath());
        assertEquals("/uu/aac",
                trieMapping.findRoute("GET", "/uu/aac").getPath());
        assertEquals("/abc/def",
                trieMapping.findRoute("POST", "/abc/def").getPath());
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
        assertEquals("/aaa/:id/:name", route.getPath());
        assertEquals(map(Arrays.asList(
                Pair.of("id", "mm"),
                Pair.of("name", "hh")
        )), route.getPathParams());

        route = trieMapping.findRoute("GET", "/pp/name/lala");
        assertEquals("/pp/name/:ppp", route.getPath());
        assertEquals(map(Arrays.asList(
                Pair.of("ppp", "lala")
        )), route.getPathParams());

        route = trieMapping.findRoute("GET", "/cc/ok/oq");
        assertEquals("/cc/:name/oq", route.getPath());
        assertEquals(map(Arrays.asList(
                Pair.of("name", "ok")
        )), route.getPathParams());

        assertNull(trieMapping.findRoute("GET", "cc/ok"));
        assertNull(trieMapping.findRoute("GET", "/aaa/mm"));
    }

    @Test
    public void testCatchAll() {
        TrieMapping trieMapping = initMapping(Arrays.asList(
                MethodAndPath.of(HttpMethod.GET, "/child/*"),
                MethodAndPath.of(HttpMethod.GET, "/ooo/*"))
        );

        assertEquals("/child/*",
                trieMapping.findRoute("GET", "/child/pp").getPath());
        assertEquals("/child/*",
                trieMapping.findRoute("GET", "/child/pp/ooo").getPath());
        assertEquals("/ooo/*",
                trieMapping.findRoute("GET", "/ooo/p").getPath());
    }

    @Test
    public void testShareDynamicNode() {
        TrieMapping trieMapping = initMapping(Arrays.asList(
                MethodAndPath.of(HttpMethod.GET, "/child/:id/ccc"),
                MethodAndPath.of(HttpMethod.GET, "/child/:id/ddd"))
        );

        assertEquals("/child/:id/ccc",
                trieMapping.findRoute("GET", "/child/iocs/ccc").getPath());
        assertEquals("/child/:id/ddd",
                trieMapping.findRoute("GET", "/child/oooccc/ddd").getPath());
    }

    @Test(expected = IllegalStateException.class)
    public void testConflict() {
        initMapping(Arrays.asList(
                MethodAndPath.of(HttpMethod.GET, "/child/*"),
                MethodAndPath.of(HttpMethod.GET, "/child/:pp")
        ));
    }

    @Test
    public void testAddTwoMethodOfOneUrl() {
        TrieMapping trieMapping = initMapping(Arrays.asList(
                MethodAndPath.of(HttpMethod.GET, "/child/ccc"),
                MethodAndPath.of(HttpMethod.POST, "/child/ccc"),
                MethodAndPath.of(HttpMethod.GET, "/child/mm/:po"),
                MethodAndPath.of(HttpMethod.POST, "/child/mm/:po"))
        );

        assertEquals("/child/ccc",
                trieMapping.findRoute("GET", "/child/ccc").getPath());
        assertEquals("/child/ccc",
                trieMapping.findRoute("POST", "/child/ccc").getPath());
        assertEquals("/child/mm/:po",
                trieMapping.findRoute("GET", "/child/mm/cdcr").getPath());
        assertEquals("/child/mm/:po",
                trieMapping.findRoute("POST", "/child/mm/lala").getPath());
        assertNull(trieMapping.findRoute("PUT", "/child/mm/lala"));
    }

    @Test
    public void testStaticEnd() {
        TrieMapping trieMapping = initMapping(Arrays.asList(
                        MethodAndPath.of(HttpMethod.GET, "/child/ccc/:id/number"),
                        MethodAndPath.of(HttpMethod.GET, "/child/ccc/trunk"),
                        MethodAndPath.of(HttpMethod.GET, "/child/ccc/box"),
                        MethodAndPath.of(HttpMethod.GET, "/adm/yonghu/new"),
                        MethodAndPath.of(HttpMethod.GET, "/adm/yonghu/:yonghuId/yonghu")
                )
        );

        assertEquals("/child/ccc/box",
                trieMapping.findRoute("GET", "/child/ccc/box").getPath());
        assertEquals("/child/ccc/trunk",
                trieMapping.findRoute("GET", "/child/ccc/trunk").getPath());
        assertEquals("/child/ccc/:id/number",
                trieMapping.findRoute("GET", "/child/ccc/234/number").getPath());
        assertEquals("/adm/yonghu/new",
                trieMapping.findRoute("GET", "/adm/yonghu/new").getPath());
        assertEquals("/adm/yonghu/:yonghuId/yonghu",
                trieMapping.findRoute("GET", "/adm/yonghu/afaefeaf/yonghu").getPath());
    }

    @Test
    public void testMerge() {
        TrieMapping trieMapping = initMapping(Arrays.asList(
//                        MethodAndPath.of(HttpMethod.GET, "/users/**"),
//                        MethodAndPath.of(HttpMethod.GET, "/users"),
//                        MethodAndPath.of(HttpMethod.GET, "/users/:uid"),
//                        MethodAndPath.of(HttpMethod.GET, "/users/abcd"),
//                        MethodAndPath.of(HttpMethod.GET, "/users/:uid/abcd"),
                        MethodAndPath.of(HttpMethod.GET, "/users/abcd/:uid")
                )
        );
//        System.out.println(trieMapping.findRoute("GET", "/users"));
//        System.out.println(trieMapping.findRoute("GET", "/users/abcd"));
        System.out.println(trieMapping.findRoute("GET", "/users/abcd/123"));
//        System.out.println(trieMapping.findRoute("GET", "/users/dbcd/123"));
//        System.out.println(trieMapping.findRoute("GET", "/users/1234"));
//        System.out.println(trieMapping.findRoute("GET", "/users/1234/abcd"));
//        System.out.println(trieMapping.findRoute("GET", "/users/1234/abcd/efef"));
//        System.out.println(trieMapping.findRoute("GET", "/users/abcd/abcd/efef"));
    }

}