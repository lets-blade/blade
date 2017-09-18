package com.blade;

import com.blade.security.web.auth.BasicAuthMiddleware;
import org.junit.Test;
import sun.misc.BASE64Encoder;

import static org.junit.Assert.assertEquals;

/**
 * Middleware Test
 *
 * @author biezhi
 * 2017/6/5
 */
public class MiddlewareTest extends BaseTestCase {

    @Test
    public void testMiddleware() throws Exception {
        start(
                app.use(signature -> {
                    signature.request().attribute("middleware", "2017");
                    return signature.next();
                }).get("/", ((request, response) -> response.text(request.attribute("middleware"))))
        );
        String result = bodyToString("/");
        assertEquals("2017", result);
    }

    @Test
    public void testAuthMiddleware() throws Exception {
        start(
                app.use(new BasicAuthMiddleware()).get("/", ((request, response) -> response.text("Hello")))
        );

        int code = get("/").asString().getStatus();
        assertEquals(401, code);

        String basicAuth = new BASE64Encoder().encode("blade:blade".getBytes());
        String result    = get("/").header("Authorization", "Basic " + basicAuth).asString().getBody();
        assertEquals("Hello", result);
    }
}
