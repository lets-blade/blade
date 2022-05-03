package com.hellokaton.blade.mvc.route;

import com.hellokaton.blade.annotation.route.GET;
import com.hellokaton.blade.annotation.Path;
import com.hellokaton.blade.mvc.hook.WebHook;
import com.hellokaton.blade.mvc.http.Response;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author biezhi
 * @date 2017/9/19
 */
public class ANYBuilderTest {

    @Test
    public void testCreateRouteBuilder() throws Exception {
        RouteMatcher routeMatcher = new RouteMatcher();
        RouteBuilder routeBuilder = new RouteBuilder(routeMatcher);
        routeBuilder.addRouter(DemoController.class, new DemoController());
        routeBuilder.addWebHook(WebHook.class, "/*");

        routeMatcher.register();

        Route route = routeMatcher.lookupRoute("GET", "/");
        Assert.assertNotNull(route);
        Assert.assertEquals("GET\t/\t/", route.toString());
    }

    @Path
    class DemoController {
        @GET
        public void hello(Response response) {
            response.text("Ok.");
        }
    }

}
