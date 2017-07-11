package com.blade;

import org.junit.Test;

/**
 * Middleware Test
 *
 * @author biezhi
 *         2017/6/5
 */
public class MiddlewareTest extends BaseTestCase {

    private String defaultKey = "csrf_token";

    @Test
    public void testMiddleware() throws Exception {
        start(
                app.use((invoker) -> {
                    System.out.println(invoker.request().uri());
                    return invoker.next();
                }).get("/login", ((request, response) -> response.text(request.attribute(defaultKey))))
        );
        String token = bodyToString("/login");
        System.out.println(token);
    }

}
