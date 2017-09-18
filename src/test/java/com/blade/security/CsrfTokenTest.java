package com.blade.security;

import com.blade.BaseTestCase;
import com.blade.mvc.handler.RouteHandler;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;
import com.blade.security.web.csrf.CsrfMiddleware;
import com.blade.security.web.csrf.CsrfToken;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * CsrfToken Test
 *
 * @author biezhi
 * 2017/6/5
 */
@Slf4j
public class CsrfTokenTest extends BaseTestCase {

    private String defaultKey = "csrf_token";
    private String validId    = "_csrf.valid";

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
        log.info("token: {}", token);
        String body = post("/login").header(validId, "true")
                .queryString("csrf_token", token)
                .asString().getBody();

        assertEquals("登录成功", body);
    }

    @Test
    public void testValidTokenFail() throws Exception {

        start(
                app.use(new CsrfMiddleware())
                        .get("/login", new CsrfTokenClass())
                        .post("/login", new CsrfTokenValid())
        );

        String token = bodyToString("/login");
        log.info("token: {}", token);
        token = "";
        String body = post("/login").header(validId, "true")
                .queryString("csrf_token", token).asString().getBody();

        assertEquals("Bad Request.", body);
    }

    class CsrfTokenClass implements RouteHandler {
        @CsrfToken(newToken = true)
        @Override
        public void handle(Request request, Response response) {
            response.text(request.attribute(defaultKey));
        }
    }

    class CsrfTokenValid implements RouteHandler {
        @CsrfToken(valid = true)
        @Override
        public void handle(Request request, Response response) {
            response.text("登录成功");
        }
    }

    @Test
    public void testValidTokenNoValid() throws Exception {
        start(
                app.use(new CsrfMiddleware()).get("/login", ((request, response) -> response.text(request.attribute(defaultKey)))).post("/login", ((request, response) -> response.text("登录成功")))
        );

        String token = bodyToString("/login");
        System.out.println(token);
        token = "";
        String body = post("/login").queryString("csrf_token", token).asString().getBody();
        assertEquals("登录成功", body);
    }

}
