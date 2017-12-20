package io.example.blog.controller;

import com.blade.mvc.annotation.GetRoute;
import com.blade.mvc.annotation.Path;
import io.example.blog.model.User;

/**
 * @author biezhi
 * @date 2017/12/20
 */
@Path("auth")
public class AuthController {

    @GetRoute("login")
    public void login(User user) {
        System.out.println("login: " + user);
    }

}
