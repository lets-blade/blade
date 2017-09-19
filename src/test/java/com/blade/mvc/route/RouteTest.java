package com.blade.mvc.route;

import com.blade.mvc.http.HttpMethod;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * @author biezhi
 * @date 2017/9/19
 */
public class RouteTest {

    private Route route;

    @Before
    public void before(){
        route = new Route();
    }

    @Test
    public void testBuildRoute(){
        route.toString();
        Route route2 = new Route(HttpMethod.GET, "/", null, null);
        assertNotEquals(route, route2);
    }

    @Test
    public void testSort(){
        assertEquals(Integer.MAX_VALUE, route.getSort());

        route.setSort(20);
        assertEquals(20, route.getSort());
    }

    @Test
    public void testPath(){
        assertEquals(null, route.getPath());

        route.setPath("/a");
        assertEquals("/a", route.getPath());
    }

    @Test
    public void testHttpMethod(){
        assertEquals(null, route.getHttpMethod());

        route = new Route(HttpMethod.DELETE, "/", null, null);
        assertEquals(HttpMethod.DELETE, route.getHttpMethod());
    }

    @Test
    public void testPathParams(){
        assertEquals(0, route.getPathParams().size());

        Map<String, String> map = new HashMap<>();
        map.put("name", "jack");
        map.put("age", "22");
        route.setPathParams(map);

        assertEquals(2, route.getPathParams().size());
        assertEquals("jack", route.getPathParams().get("name"));
    }
}
