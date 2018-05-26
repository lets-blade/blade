package netty_hello;

import com.blade.mvc.annotation.*;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;
import com.blade.mvc.multipart.FileItem;

import java.util.Arrays;
import java.util.List;

/**
 * @author biezhi
 * @date 2018/4/18
 */
@Path
public class DemoController {

    @GetRoute("p")
    public void p(@Param String p1) {
        System.out.println(p1);
    }

    @Route("hi/:a/:b/:c")
    public void pathParam(Request request) {
        System.out.println(request.pathString("a"));
        System.out.println(request.pathString("b"));
        System.out.println(request.pathString("c"));
    }

    @PostRoute("ck")
    public void postCk(@Param(name = "ck") List<String> a, String[] ck, Integer[] ids) {
        System.out.println("a: " + a);
        System.out.println("ck: " + Arrays.toString(ck));
        System.out.println("ids: " + Arrays.toString(ids));
    }

    @PostRoute("def")
    public void postCk(@Param String hello, @Param Integer aa, @Param int bb) {
        System.out.println("hello:" + hello);
        System.out.println("aa:" + aa);
        System.out.println("bb:" + bb);
    }

    @GetRoute("csrf")
    public void getCsrfToken(Request request, Response response) {
        response.text("token: " + request.attribute("_csrf_token"));
    }

    @PostRoute("upload")
    public void upload(@MultipartParam FileItem fileItem) {
        System.out.println(fileItem.getFileName());
        System.out.println(fileItem.getContentType());
        System.out.println(fileItem.getLength());
    }

}
