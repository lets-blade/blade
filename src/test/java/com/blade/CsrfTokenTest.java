package com.blade;

import com.blade.mvc.middlewares.CsrfMiddleware;
import org.junit.Test;

/**
 * @author biezhi
 *         2017/6/5
 */
public class CsrfTokenTest extends BaseTestCase {

    @Test
    public void testGenToken() throws Exception {
        start(
                app.use(new CsrfMiddleware()).get("/login", ((request, response) -> {
                    response.text(request.attribute(CsrfMiddleware.CSRF_TOKEN));
                }))
        );
        String token = bodyToString("/login");
        System.out.println(token);
    }

    @Test
    public void testValidToken() throws Exception {
        start(
                app.use(new CsrfMiddleware()).get("/login", ((request, response) -> {
                    response.text(request.attribute(CsrfMiddleware.CSRF_TOKEN));
                })).post("/login", ((request, response) -> {
                    if (CsrfMiddleware.validation()) {
                        response.text("登录成功");
                    }
                }))
        );

        String token = bodyToString("/login");
        System.out.println(token);
        token = "";
        String body = post("/login").form("csrf_token", token).body();
        System.out.println(body);
    }

}
