package io.example.blog.controller;

import com.blade.mvc.annotation.GetRoute;
import com.blade.mvc.annotation.Path;

/**
 * @author biezhi
 * @date 2017/12/20
 */
@Path("auth")
public class AuthController {

    @GetRoute("login")
    public void login() {
        System.out.println("login");
    }

}
