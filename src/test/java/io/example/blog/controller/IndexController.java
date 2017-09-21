package io.example.blog.controller;

import com.blade.ioc.annotation.Inject;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author biezhi
 * 2017/5/31
 */
@Path
public class IndexController {

    @Inject
    private AService aService;

    @GetRoute(value = "/hello")
    public void index(Response response) {
//        int a = 1/0;
        response.text("hello world!");
    }

    @GetRoute(value = "/user")
    public String userPage(Request request, Session session) {
        request.attribute("name", "biezhi");
        request.attribute("github", "https://github.com/biezhi");

        Map<String, String> map = new HashMap<>();
        map.put("a", "222");
        map.put("b", "333");
        request.attribute("m.a", "asdadasd");
        request.attribute("m", map);

        if (null != session) {
            session.attribute("loginUser", "admin");
        }
        return "user.html";
    }

    @PostRoute("update_article")
    public void updateArticle(Optional<Article> article) {
        System.out.println(article);
    }

    @PostRoute(value = "/save")
    @JSON
    public RestResponse saveArticle(@BodyParam Article article, Request request) {
        System.out.println(article);
        if (null == article) {
            System.out.println(request.bodyToString());
        }
        return RestResponse.ok();
    }

    @PostRoute(value = "upload")
    @JSON
    public RestResponse upload(@MultipartParam("img2") FileItem fileItem) {
        System.out.println(fileItem);
        return RestResponse.ok();
    }

    @GetRoute(value = "exp1")
    @JSON
    public void exp1() {
        int a = 1 / 0;
    }

    @GetRoute(value = "exp2")
    public void exp2() {
        aService.exp();
    }

    @GetRoute(value = "empty")
    public void empty() {
        System.out.println("empty request");
    }

    @GetRoute(value = "download")
    public void download(Response response) throws Exception {
        String path = Const.CLASSPATH + File.separator + "static/a.txt";
        response.download("文件.txt", new File(path));
    }

    @GetRoute(value = "redirect")
    public void redirect(@Param String url, Response response) {
        response.redirect(url);
    }
}
