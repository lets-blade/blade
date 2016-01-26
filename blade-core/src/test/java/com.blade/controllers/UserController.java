package com.blade.controllers;

import com.blade.web.http.Request;
import com.blade.web.http.Response;

/**
 * @author xieenlong
 * @date 16/1/26.
 */
public class UserController {

    public void all(Request request, Response response) {
        response.html("all users");
    }
}
