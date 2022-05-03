package com.blade.security.web;

import com.blade.Blade;
import com.blade.mvc.RouteContext;
import com.blade.mvc.WebContext;
import com.blade.mvc.handler.RouteHandler;
import com.blade.mvc.http.HttpRequest;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;
import com.blade.mvc.route.Route;
import com.blade.security.web.auth.AuthOption;
import com.blade.security.web.auth.BasicAuthMiddleware;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

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

        Request mockRequest = mockHttpRequest("GET");

        WebContext.init(Blade.of(), "/");

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Basic YWRtaW46MTIzNDU2");

        Mockito.when(mockRequest.formParams()).thenReturn(new HashMap<>());
        Mockito.when(mockRequest.headers()).thenReturn(headers);

        Request  request  = new HttpRequest(mockRequest);
        Response response = mockHttpResponse(200);

        RouteContext context = new RouteContext(request, response);
        context.initRoute(Route.builder()
                .action(AuthHandler.class.getMethod("handle", RouteContext.class))
                .targetType(AuthHandler.class)
                .target(new AuthHandler()).build());

        WebContext.set(new WebContext(request, response, null));

        AuthOption authOption = AuthOption.builder().build();
        authOption.addUser("admin", "123456");

        BasicAuthMiddleware basicAuthMiddleware = new BasicAuthMiddleware(authOption);
        boolean             flag                = basicAuthMiddleware.before(context);
        Assert.assertTrue(flag);
    }

    @Test
    public void testAuthFail() throws Exception {
        Request mockRequest = mockHttpRequest("GET");

        WebContext.init(Blade.of(), "/");

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Basic YmxhZGU6YmxhZGUyMg==");

        Mockito.when(mockRequest.formParams()).thenReturn(new HashMap<>());
        Mockito.when(mockRequest.headers()).thenReturn(headers);

        Request  request  = new HttpRequest(mockRequest);
        Response response = mockHttpResponse(200);

        RouteContext context = new RouteContext(request, response);

        context.initRoute(Route.builder()
                .action(AuthHandler.class.getMethod("handle", RouteContext.class))
                .targetType(AuthHandler.class)
                .target(new AuthHandler()).build());

        WebContext.set(new WebContext(request, response, null));

        AuthOption authOption = AuthOption.builder().build();
        authOption.addUser("admin", "123456");

        BasicAuthMiddleware basicAuthMiddleware = new BasicAuthMiddleware(authOption);
        boolean             flag                = basicAuthMiddleware.before(context);
        Assert.assertFalse(flag);
    }

}
