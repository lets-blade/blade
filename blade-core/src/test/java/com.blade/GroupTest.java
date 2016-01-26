package com.blade;

import com.blade.route.RouteGroup;
import com.blade.route.RouteHandler;
import com.blade.web.http.Request;
import com.blade.web.http.Response;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * @author xieenlong
 * @date 16/1/26.
 */
public class GroupTest {

    @Test
    public void nestedGroup() {
        Blade blade = Blade.me();
        blade.group("/", new RouteGroup() {
            @Override
            public void route() {
                get("posts", new RouteHandler() {
                    @Override
                    public void handle(Request request, Response response) {
                        response.html("all posts");
                    }
                });

                group("users", new RouteGroup() {
                    @Override
                    public void route() {
                        get("", new RouteHandler() {
                            @Override
                            public void handle(Request request, Response response) {
                                response.html("all users");
                            }
                        });

                        group(":userId/posts", new RouteGroup() {
                            @Override
                            public void route() {
                                get("", new RouteHandler() {
                                    @Override
                                    public void handle(Request request, Response response) {
                                        response.html("user " + request.param("userId") + "'s all posts");
                                    }
                                });

                                get(":postId", new RouteHandler() {
                                    @Override
                                    public void handle(Request request, Response response) {
                                        response.html("user " + request.param("userId") + "'s post " + request.param("postId"));
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
        List<String> keys = Arrays.asList("/users/:userId/posts#GET", "/users#GET", "/posts#GET", "/users/:userId/posts/:postId#GET");
        Assert.assertEquals(blade.routers().getRoutes().size(), 4);
        Assert.assertTrue(blade.routers().getRoutes().keySet().containsAll(keys));
    }

    @Test
    public void nestedGroupConfig() {
        Blade blade = Blade.me();
        blade.routeConf("com.blade.controllers", "route.conf");
        List<String> keys = Arrays.asList("/users/:userId/posts#GET", "/users#GET", "/posts#GET", "/users/:userId/posts/:postId#GET");
        Assert.assertEquals(blade.routers().getRoutes().size(), 4);
        Assert.assertTrue(blade.routers().getRoutes().keySet().containsAll(keys));
    }
}
