package com.blade.mvc.route;

import com.blade.mvc.handler.RouteHandler;
import com.blade.mvc.http.HttpMethod;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * @author biezhi
 * @date 2017/9/19
 */
public class RouteMatcherTest {

    private RouteMatcher routeMatcher;

    @Before
    public void before() {
        routeMatcher = new RouteMatcher();
    }

    @Test
    public void testRouteMatcher() throws Exception {
        routeMatcher.addRoute("/", (req, res) -> res.text("Ok"), HttpMethod.GET);
        routeMatcher.addRoute("/*", (req, res) -> res.text("Ok"), HttpMethod.BEFORE);

        routeMatcher.register();

        Route route = routeMatcher.lookupRoute("GET", "/");
        Assert.assertEquals("GET\t/", route.toString());

        List<Route> routes = routeMatcher.getBefore("/");
        Assert.assertEquals(1, routes.size());


    }

    @Test
    public void testAddRoute() throws Exception {
        routeMatcher.addRoute(Route.builder().httpMethod(HttpMethod.POST).targetType(RouteHandler.class)
                .target((RouteHandler) (request, response) -> {
                    response.text("post request");
                })
                .path("/save")
                .build());

        routeMatcher.register();

        Route saveRoute = routeMatcher.lookupRoute("POST", "/save");
        Assert.assertEquals("POST\t/save", saveRoute.toString());

    }

    @Test
    public void testAddMultiParameter() throws Exception {
        routeMatcher.route("/index", RouteMatcherDemoController.class, "index");

        routeMatcher.route("/remove", RouteMatcherDemoController.class, "remove", HttpMethod.DELETE);

        routeMatcher.register();

        Route route = routeMatcher.lookupRoute("GET", "/index");
        Assert.assertEquals("ALL\t/index", route.toString());

        Route removeRoute = routeMatcher.lookupRoute("DELETE", "/remove");
        Assert.assertEquals("DELETE\t/remove", removeRoute.toString());

    }

    class RouteMatcherDemoController {
        public void index() {
        }

        public void remove() {
        }
    }

}
