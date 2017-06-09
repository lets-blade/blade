package io.example.blog.controller;

import com.blade.ioc.annotation.Inject;
import com.blade.kit.StringKit;
import com.blade.mvc.Const;
import com.blade.mvc.annotation.*;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;
import com.blade.mvc.http.Session;
import com.blade.mvc.multipart.FileItem;
import com.blade.mvc.ui.RestResponse;
import io.example.blog.model.Article;
import io.example.blog.service.AService;

import java.io.File;

/**
 * @author biezhi
 *         2017/5/31
 */
@Path
public class IndexController {

    @Inject
    private AService aService;

    @GetRoute(values = "/hello")
    public void index(Response response) {
//        aService.sayHi();
        response.text("hello world!");
    }

    @GetRoute(values = "/user")
    public String userPage(Request request, Session session) {
        request.attribute("name", "biezhi");
        request.attribute("github", "https://github.com/biezhi");
        if (null != session) {
            session.attribute("loginUser", "admin");
        }
        return "user.html";
    }

    @PostRoute(values = "/save")
    @JSON
    public RestResponse saveArticle(@BodyParam Article article, Request request) {
        System.out.println(article);
        if (null == article) {
            System.out.println(request.bodyToString());
        }
        return RestResponse.ok();
    }

    @PostRoute(values = "upload")
    @JSON
    public RestResponse upload(@MultipartParam("img2") FileItem fileItem) {
        System.out.println(fileItem);
        return RestResponse.ok();
    }

    @GetRoute(values = "exp1")
    @JSON
    public void exp1() {
        int a = 1 / 0;
    }

    @GetRoute(values = "exp2")
    public void exp2() {
        aService.exp();
    }

    @GetRoute(values = "empty")
    public void empty() {
//        System.out.println("empty request");
    }

    @GetRoute(values = "download")
    public void download(Response response) throws Exception {
        String path = Const.CLASSPATH + "static/a.txt";
        response.donwload("文件.txt", new File(path));
    }

    @GetRoute(values = "redirect")
    public void redirect(@QueryParam String url, Response response) {
        response.redirect(url);
    }
}
