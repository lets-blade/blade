package com.blade.controllers;

import com.blade.web.http.Request;
import com.blade.web.http.Response;

/**
 * @author xieenlong
 * @date 16/1/26.
 */
public class PostController {

    public void all(Request request, Response response) {
        response.html("all posts");
    }

    public void byAuthor(Request request, Response response) {
        response.html("user " + request.param("userId") + "'s all posts");
    }

    public void get(Request request, Response response) {
        response.html("user " + request.param("userId") + "'s post " + request.param("postId"));
    }
}
