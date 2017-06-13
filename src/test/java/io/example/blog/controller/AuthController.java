package io.example.blog.controller;

import com.blade.mvc.annotation.GetRoute;
import com.blade.mvc.annotation.Path;
import com.blade.mvc.annotation.PostRoute;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;
import com.blade.mvc.middlewares.CsrfMiddleware;

/**
 * @author biezhi
 *         2017/6/5
 */
@Path
public class AuthController {

    @GetRoute("login")
    public void login(Request request, Response response) {
        response.text(request.attribute(CsrfMiddleware.CSRF_TOKEN));
    }

    @PostRoute(value = "login")
    @CsrfMiddleware.ValidToken
    public void doLogin(Response response) {
        response.text("登录成功");
    }

}
