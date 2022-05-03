package com.example;

import com.blade.Blade;
import com.blade.annotation.Path;
import com.blade.annotation.request.Body;
import com.blade.annotation.request.Form;
import com.blade.annotation.request.Multipart;
import com.blade.annotation.response.Response;
import com.blade.annotation.route.GET;
import com.blade.annotation.route.POST;
import com.blade.mvc.HttpConst;
import com.blade.mvc.http.Request;
import com.blade.mvc.multipart.FileItem;
import com.blade.options.CorsOptions;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Path(responseJson = true)
public class Application {

    @GET("/hello")
    public Map<String, Object> hello(Request req) {
        Map<String, Object> result = new HashMap<>();
        result.put("name", "hellokaton");
        Map<String, List<String>> queries = req.queries();
        for (Map.Entry<String, List<String>> entry : queries.entrySet()) {
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

    @POST("/upload")
    @Response(contentType = HttpConst.CONTENT_TYPE_TEXT)
    public String upload(@Multipart FileItem fileItem) throws IOException {
        log.info("读取到 fileItem = {}", fileItem);
        fileItem.moveTo(new File(fileItem.getFileName()));
        return fileItem.getFileName();
    }

    public static void main(String[] args) {
        CorsOptions corsOptions = CorsOptions.forAnyOrigin().allowNullOrigin().allowCredentials();
        Blade.of().cors(corsOptions).listen().start(Application.class);
    }

}
