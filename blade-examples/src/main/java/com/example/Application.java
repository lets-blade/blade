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
import com.hellokaton.blade.mvc.multipart.FileItem;
import com.hellokaton.blade.mvc.ui.ResponseType;
import com.hellokaton.blade.options.CorsOptions;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Path(responseType = ResponseType.JSON)
public class Application {

    @GET("/hello")
    public Map<String, Object> hello(Request req) {
        Map<String, Object> result = new HashMap<>();
        result.put("name", "hellokaton");
        Map<String, List<String>> params = req.queryParams();
        for (Map.Entry<String, List<String>> entry : params.entrySet()) {
            result.put(entry.getKey(), entry.getValue());
        }
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
    public Result<?> deleteUser(@PathParam String uid) {
        log.info("删除 uid = {}", uid);
        return Result.success(uid);
    }

    @POST(value = "/upload", responseType = ResponseType.TEXT)
    public String upload(@Multipart FileItem fileItem) throws IOException {
        log.info("读取到 fileItem = {}", fileItem);
        fileItem.moveTo(new File(fileItem.getFileName()));
        return fileItem.getFileName();
    }

    public static void main(String[] args) {
        CorsOptions corsOptions = CorsOptions.forAnyOrigin().allowNullOrigin().allowCredentials();
        Blade.create()
                .cors(corsOptions)
                .listen().start(Application.class);
    }

}
