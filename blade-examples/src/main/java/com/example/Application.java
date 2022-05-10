package com.example;

import com.hellokaton.blade.Blade;
import com.hellokaton.blade.annotation.Path;
import com.hellokaton.blade.annotation.request.Body;
import com.hellokaton.blade.annotation.request.Form;
import com.hellokaton.blade.annotation.request.Multipart;
import com.hellokaton.blade.annotation.request.PathParam;
import com.hellokaton.blade.annotation.route.ANY;
import com.hellokaton.blade.annotation.route.DELETE;
import com.hellokaton.blade.annotation.route.GET;
import com.hellokaton.blade.annotation.route.POST;
import com.hellokaton.blade.mvc.http.ByteBody;
import com.hellokaton.blade.mvc.http.Request;
import com.hellokaton.blade.mvc.http.Response;
import com.hellokaton.blade.mvc.http.StaticFileBody;
import com.hellokaton.blade.mvc.multipart.FileItem;
import com.hellokaton.blade.mvc.multipart.MimeType;
import com.hellokaton.blade.mvc.ui.ResponseType;
import com.hellokaton.blade.mvc.ui.RestResponse;
import com.hellokaton.blade.options.CorsOptions;
import com.hellokaton.blade.options.HttpOptions;
import com.hellokaton.blade.security.limit.LimitOptions;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
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
    public String index(Request req) {
        String token = req.attribute("_csrf_token");
        System.out.println("token = " + token);
        token = null == token ? "token is null" : token;
        return token;
    }

    @GET(value = "home", responseType = ResponseType.VIEW)
    public String home() {
        return "home.html";
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

    @ANY(value = "/hello/:id", responseType = ResponseType.JSON)
    public RestResponse<String> helloAny(@PathParam String id) {
        return RestResponse.success(id);
    }

    @GET(value = "/ss", responseType = ResponseType.PREVIEW)
    public StaticFileBody staticFile() {
        return StaticFileBody.of("/static/main.css");
    }

    @GET(value = "/img")
    public void img(Response response) throws IOException {
        ByteBufOutputStream out = new ByteBufOutputStream(Unpooled.buffer());
        int width = 200, height = 250;
        //创建图片对象
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        //基于图片对象打开绘图
        Graphics2D graphics = image.createGraphics();
        //绘图逻辑 START （基于业务逻辑进行绘图处理）……

        //绘制圆形
        graphics.setColor(Color.BLACK);
        Ellipse2D.Double ellipse = new Ellipse2D.Double(20, 20, 100, 100);
        graphics.draw(ellipse);
        // 绘图逻辑 END
        //处理绘图
        graphics.dispose();
        //将绘制好的图片写入到图片
//        ImageIO.write(image, "PNG", new FileOutputStream("aaa.png"));
        ImageIO.write(image, "PNG", out);
        response.contentType("image/png");
        response.body(new ByteBody(out.buffer()));
    }

    @GET(value = "/public/**", responseType = ResponseType.PREVIEW)
    public StaticFileBody publicDir() {
        return StaticFileBody.of("/static/");
    }

    public static void main(String[] args) {
        CorsOptions corsOptions = CorsOptions.forAnyOrigin().allowNullOrigin().allowCredentials();

        LimitOptions limitOptions = LimitOptions.create();
        limitOptions.setExpression("1/s");

        Blade.create()
                .cors(corsOptions)
                .http(HttpOptions::enableSession)
                .get("/base/:uid", ctx -> {
                    ctx.text(ctx.pathString("uid"));
                })
                .get("/base/:uid/hello", ctx -> {
                    ctx.text(ctx.pathString("uid") + ": hello");
                })
                .before("/**", ctx -> {
                    System.out.println("bebebebebe");
                })
//                .use(new CsrfMiddleware())
//                .use(new LimitMiddleware(limitOptions))
                .start(Application.class, args);
    }

}
