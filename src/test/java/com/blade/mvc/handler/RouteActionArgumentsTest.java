package com.blade.mvc.handler;

import com.blade.mvc.RouteContext;
import com.blade.mvc.http.HttpRequest;
import com.blade.mvc.http.Request;
import com.blade.mvc.route.Route;
import com.blade.types.NotifyType;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author biezhi
 * @date 2017/9/19
 */
public class RouteActionArgumentsTest {

    private Request request;

    private RouteContext context;

    @Before
    public void before() {
        Map<String, String> params = new HashMap<>();
        params.put("uid", "20");

        request = new HttpRequest();
        request.initPathParams(Route.builder().pathParams(params).build());

        request.parameters().put("name", Arrays.asList("jack"));

        context = new RouteContext(request, null);
    }

    @Test
    public void testMethodArgs() throws Exception {
//        context.setAction(IndexController.class.getMethod("findUser", Long.class));

        Object[] args = RouteActionArguments.getRouteActionParameters(context);
        assertEquals(1, args.length);
        assertEquals(Long.valueOf(20), args[0]);
    }

    @Test
    public void testMethodParam() throws Exception {
//        context.setAction(IndexController.class.getMethod("users", String.class));
        Object[] args = RouteActionArguments.getRouteActionParameters(context);
        assertEquals(1, args.length);
        assertEquals("jack", args[0]);
    }

    @Test
    public void testMethodBodyParam() throws Exception {
        request.body().writeBytes("{\"money\":\"22\", \"oid\": \"8as8c01k3llawm\"}".getBytes());
//        context.setAction(IndexController.class.getMethod("notify", NotifyType.class));
        Object[] args = RouteActionArguments.getRouteActionParameters(context);
        assertEquals(1, args.length);
        assertEquals(NotifyType.class, args[0].getClass());
    }

}
