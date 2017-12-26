package com.blade.security;

import com.blade.BaseTestCase;
import com.blade.Blade;
import com.blade.mvc.WebContext;
import com.blade.mvc.handler.RouteHandler;
import com.blade.mvc.hook.Signature;
import com.blade.mvc.http.HttpRequest;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;
import com.blade.mvc.route.Route;
import com.blade.security.web.csrf.CsrfMiddleware;
import com.blade.security.web.csrf.CsrfToken;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * CsrfToken Test
 *
 * @author biezhi
 * 2017/6/5
 */
@Slf4j
public class CsrfMiddlewareTest extends BaseTestCase {

    private String defaultKey = "csrf_token";
    private String validId    = "_csrf.valid";

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
    public void testValidTokenSuccess() throws Exception {
        Blade blade = Blade.me()
                .use(new CsrfMiddleware())
                .get("/login", ((request, response) -> response.text(request.attribute(defaultKey))))
                .post("/login", ((request, response) -> response.text("登录成功")))
                .start().await();
        try {
            String token = bodyToString("/login");
            log.info("token: {}", token);
            String body = post("/login").header(validId, "true")
                    .queryString("csrf_token", token)
                    .asString().getBody();

            assertEquals("登录成功", body);
        } finally {
            blade.stop();
        }
    }

    @Test
    public void testValidTokenFail() throws Exception {
        Signature signature   = new Signature();
        Request   mockRequest = mockRequest("POST");

        when(mockRequest.parameters()).thenReturn(new HashMap<>());
        when(mockRequest.headers()).thenReturn(new HashMap<>());

        Request  request  = new HttpRequest(mockRequest);
        Response response = mockResponse(200);

        signature.setResponse(response);
        signature.setRequest(request);
        signature.setRoute(Route.builder()
                .action(CsrfTokenValid.class.getMethod("handle", Request.class, Response.class))
                .targetType(CsrfTokenValid.class)
                .target(new CsrfTokenValid()).build());

        WebContext.set(new WebContext(request, response));

        CsrfMiddleware csrfMiddleware = new CsrfMiddleware(response1 -> System.out.println("Bad Request."));
        boolean        flag           = csrfMiddleware.before(signature);
        assertEquals(false, flag);
    }

    @Test
    public void testValidTokenNoValid() throws Exception {
        Blade blade = Blade.me().use(new CsrfMiddleware())
                .get("/login", ((request, response) -> response.text(request.attribute(defaultKey))))
                .post("/login", ((request, response) -> response.text("登录成功")))
                .start().await();

        try {
            String token = bodyToString("/login");
            System.out.println(token);
            token = "";
            String body = post("/login").queryString("csrf_token", token).asString().getBody();
            assertEquals("登录成功", body);
        } finally {
            blade.stop();
        }
    }

}
