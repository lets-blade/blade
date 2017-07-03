package com.blade;

import com.blade.mvc.middlewares.CsrfMiddleware;
import org.junit.Test;

/**
 * @author biezhi
 *         2017/6/5
 */
public class MiddlewareTest extends BaseTestCase {

    @Test
    public void testMiddleware() throws Exception {
        start(
                app.use((invoker) -> {
                    System.out.println(invoker.request().uri());
                    return invoker.next();
                }).get("/login", ((request, response) -> {
                    response.text(request.attribute(CsrfMiddleware.CSRF_TOKEN));
                }))
        );
        String token = bodyToString("/login");
        System.out.println(token);
    }

}
