package com.blade.mvc.handler;

import com.blade.Blade;
import com.blade.mvc.hook.Signature;
import com.blade.mvc.http.HttpMethod;
import com.blade.mvc.http.HttpRequest;
import com.blade.mvc.http.HttpResponse;
import com.blade.mvc.route.Route;
import com.blade.mvc.route.RouteMatcher;
import com.blade.types.BladeWebHookType;
import com.blade.types.controller.IndexController;
import org.junit.Test;

/**
 * @author biezhi
 * @date 2017/9/21
 */
public class RequestInvokerTest {

    @Test
    public void testRequestHandler() throws Exception {
        RequestInvoker requestInvoker = new RequestInvoker(Blade.me());
        Signature      signature      = new Signature();
        signature.setRequest(new HttpRequest());
        signature.setRoute(Route.builder()
                .target(new IndexController())
                .action(IndexController.class.getMethod("users", String.class))
                .build());
        requestInvoker.handle(signature);
    }

    @Test
    public void testRequestHandlerWebHook() throws Exception {
        RequestInvoker requestInvoker = new RequestInvoker(Blade.me());
        Signature      signature      = new Signature();
        signature.setRequest(new HttpRequest());
        signature.setResponse(new HttpResponse());
        signature.setParameters(new Object[]{signature.request(), signature.response()});
        signature.setRoute(Route.builder()
                .target(new IndexController())
                .action(IndexController.class.getMethod("users", String.class))
                .httpMethod(HttpMethod.GET)
                .targetType(IndexController.class)
                .path("/users")
                .build());

        Route route = new RouteMatcher().addRoute("/*", new BladeWebHookType(), HttpMethod.BEFORE);
        requestInvoker.invokeHook(signature, route);
    }

}
