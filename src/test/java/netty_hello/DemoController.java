package netty_hello;

import com.blade.mvc.annotation.*;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;

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

    @GetRoute("csrf")
    public void getCsrfToken(Request request, Response response) {
        response.text("token: " + request.attribute("_csrf_token"));
    }

}
