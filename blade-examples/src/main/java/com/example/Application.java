package com.example;

import com.hellokaton.blade.Blade;
import com.hellokaton.blade.annotation.Path;
import com.hellokaton.blade.annotation.request.Body;
import com.hellokaton.blade.annotation.request.Form;
import com.hellokaton.blade.annotation.request.Multipart;
import com.hellokaton.blade.annotation.request.PathParam;
import com.hellokaton.blade.annotation.route.DELETE;
import com.hellokaton.blade.annotation.route.GET;
import com.hellokaton.blade.annotation.route.POST;
import com.hellokaton.blade.mvc.http.Request;
import com.hellokaton.blade.mvc.http.Response;
import com.hellokaton.blade.mvc.multipart.FileItem;
import com.hellokaton.blade.mvc.ui.ResponseType;
import com.hellokaton.blade.mvc.ui.RestResponse;
import com.hellokaton.blade.options.CorsOptions;
import com.hellokaton.blade.options.HttpOptions;
import com.hellokaton.blade.security.limit.LimitOptions;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Path(responseType = ResponseType.JSON)
public class Application {

    @GET("/hello")
    public Map<String, Object> hello(Request req) {
        Map<String, Object> result = new HashMap<>();
        result.put("name", "hellokaton");
        result.putAll(req.queryParams());
        return result;
    }

    @POST("/read_body")
    public String readBody(@Body String body) {
        log.info("读取到 body = {}", body);
        return body;
    }

    @POST("/form_data")
    public String formData(Request req, @Form Integer age) {
        log.info("读取到 form = {}", req.formParams());
        log.info("读取到 age = {}", age);
        return "hello";
    }

    @DELETE("/users/:uid")
    public RestResponse<?> deleteUser(@PathParam String uid) {
        log.info("删除 uid = {}", uid);
        return RestResponse.success(uid);
    }

    @POST(value = "/upload", responseType = ResponseType.TEXT)
    public String upload(@Multipart FileItem fileItem) throws IOException {
        log.info("读取到 fileItem = {}", fileItem);
        fileItem.moveTo(new File(fileItem.getFileName()));
        return fileItem.getFileName();
    }

    @GET
    public String home(Request req) {
        String token = req.attribute("_csrf_token");
        System.out.println("token = " + token);
        return token;
    }

    @POST
    public String verifyToken(Request req) {
        System.out.println("token = " + req.header("X-CSRF-TOKEN"));
        return "nice.. :)";
    }

    @GET(value = "/preview/:id", responseType = ResponseType.PREVIEW)
    public void preview(@PathParam String id, Response response) throws IOException {
        response.write(new File("/Users/biezhi/Downloads/146373013842336153820220427172437.pdf"));
    }

    @GET(value = "/file/:id", responseType = ResponseType.STREAM)
    public void download(@PathParam String id, Response response) throws Exception {
        response.write("abcd.pdf", new File("/Users/biezhi/Downloads/146373013842336153820220427172437.pdf"));
    }

    public static void main(String[] args) {
        CorsOptions corsOptions = CorsOptions.forAnyOrigin().allowNullOrigin().allowCredentials();

        LimitOptions limitOptions = LimitOptions.create();
        limitOptions.setExpression("2/s");

        Blade.create()
                .cors(corsOptions)
                .http(HttpOptions::enableSession)
//                .use(new CsrfMiddleware())
//                .use(new LimitMiddleware(limitOptions))
                .listen().start(Application.class);
    }

}
