package com.blade.security;

import com.blade.BaseTestCase;
import com.blade.Blade;
import com.blade.mvc.RouteContext;
import com.blade.mvc.WebContext;
import com.blade.mvc.handler.RouteHandler;
import com.blade.mvc.http.HttpRequest;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;
import com.blade.mvc.route.Route;
import com.blade.security.web.auth.BasicAuthMiddleware;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * CsrfToken Test
 *
 * @author biezhi
 * 2017/6/5
 */
@Slf4j
public class BasicAuthMiddlewareTest extends BaseTestCase {

    class AuthHandler implements RouteHandler {
        @Override
        public void handle(RouteContext context) {
            context.text("登录成功");
        }

    }

    @Test
    public void testAuthSuccess() throws Exception {

        Request   mockRequest = mockHttpRequest("GET");

        WebContext.init(Blade.of(), "/");


        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Basic YmxhZGU6YmxhZGU=");

        when(mockRequest.parameters()).thenReturn(new HashMap<>());
        when(mockRequest.headers()).thenReturn(headers);

        Request  request  = new HttpRequest(mockRequest);
        Response response = mockHttpResponse(200);

        RouteContext context = new RouteContext(request, response);
        context.initRoute(Route.builder()
                .action(AuthHandler.class.getMethod("handle", RouteContext.class))
                .targetType(AuthHandler.class)
                .target(new AuthHandler()).build());

        WebContext.set(new WebContext(request, response));

        BasicAuthMiddleware basicAuthMiddleware = new BasicAuthMiddleware();
        boolean             flag                = basicAuthMiddleware.before(context);
        assertEquals(false, flag);
    }

    @Test
    public void testAuthFail() throws Exception {
        Request   mockRequest = mockHttpRequest("GET");

        WebContext.init(Blade.of(), "/");

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Basic YmxhZGU6YmxhZGUyMg==");

        when(mockRequest.parameters()).thenReturn(new HashMap<>());
        when(mockRequest.headers()).thenReturn(headers);

        Request  request  = new HttpRequest(mockRequest);
        Response response = mockHttpResponse(200);

        RouteContext context = new RouteContext(request, response);

        context.initRoute(Route.builder()
                .action(AuthHandler.class.getMethod("handle", RouteContext.class))
                .targetType(AuthHandler.class)
                .target(new AuthHandler()).build());

        WebContext.set(new WebContext(request, response));

        BasicAuthMiddleware basicAuthMiddleware = new BasicAuthMiddleware();
        boolean             flag                = basicAuthMiddleware.before(context);
        assertEquals(false, flag);
    }

}
