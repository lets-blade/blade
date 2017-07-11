package com.blade;

import com.blade.security.web.csrf.CsrfMiddleware;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * CsrfToken Test
 *
 * @author biezhi
 *         2017/6/5
 */
public class CsrfTokenTest extends BaseTestCase {

    private String defaultKey = "csrf_token";
    private String validId = "_csrf.valid";

    @Test
    public void testGenToken() throws Exception {
        start(
                app.use(new CsrfMiddleware())
                        .get("/login", ((request, response) -> response.text(request.attribute(defaultKey))))
        );
        String token = bodyToString("/login");
        System.out.println(token);
    }

    @Test
    public void testValidTokenSuccess() throws Exception {
        start(
                app.use(new CsrfMiddleware()).get("/login", ((request, response) -> response.text(request.attribute(defaultKey)))).post("/login", ((request, response) -> response.text("登录成功")))
        );

        String token = bodyToString("/login");
        System.out.println(token);
        String body = post("/login").header(validId, "true").form("csrf_token", token).body();
        assertEquals("登录成功", body);
    }

    @Test
    public void testValidTokenFail() throws Exception {
        start(
                app.use(new CsrfMiddleware()).get("/login", ((request, response) -> response.text(request.attribute(defaultKey)))).post("/login", ((request, response) -> response.text("登录成功")))
        );

        String token = bodyToString("/login");
        System.out.println(token);
        token = "";
        String body = post("/login").header(validId, "true").form("csrf_token", token).body();
        assertEquals("Bad Request.", body);
    }

    @Test
    public void testValidTokenNoValid() throws Exception {
        start(
                app.use(new CsrfMiddleware()).get("/login", ((request, response) -> response.text(request.attribute(defaultKey)))).post("/login", ((request, response) -> response.text("登录成功")))
        );

        String token = bodyToString("/login");
        System.out.println(token);
        token = "";
        String body = post("/login").form("csrf_token", token).body();
        assertEquals("登录成功", body);
    }

}
