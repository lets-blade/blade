package netty_hello;

import com.hellokaton.blade.annotation.Path;
import com.hellokaton.blade.annotation.request.Body;
import com.hellokaton.blade.annotation.request.Multipart;
import com.hellokaton.blade.annotation.request.PathParam;
import com.hellokaton.blade.annotation.request.Query;
import com.hellokaton.blade.annotation.response.Response;
import com.hellokaton.blade.annotation.route.ANY;
import com.hellokaton.blade.annotation.route.GET;
import com.hellokaton.blade.annotation.route.POST;
import com.hellokaton.blade.ioc.annotation.Inject;
import com.hellokaton.blade.mvc.http.Request;
import com.hellokaton.blade.mvc.multipart.FileItem;
import com.hellokaton.blade.mvc.ui.RestResponse;
import com.hellokaton.blade.validator.Validators;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author biezhi
 * @date 2018/4/18
 */
@Path
public class DemoController {

    @Inject
    private UserService userService;

    @GET("p")
    public void p(@Query String p1) {
        System.out.println(p1);
        userService.sayHello();
    }

    @ANY("hi/:a/:b/:c")
    public void pathParam(Request request) {
        System.out.println(request.pathString("a"));
        System.out.println(request.pathString("b"));
        System.out.println(request.pathString("c"));
    }

    @POST("ck")
    public void postCk(@Query(name = "ck") List<String> a, String[] ck, Integer[] ids) {
        System.out.println("a: " + a);
        System.out.println("ck: " + Arrays.toString(ck));
        System.out.println("ids: " + Arrays.toString(ids));
    }

    @POST("def")
    public void postCk(@Query String hello, @Query Integer aa, @Query int bb) {
        System.out.println("hello:" + hello);
        System.out.println("aa:" + aa);
        System.out.println("bb:" + bb);
    }

    @Response
    @POST("api_test/:size")
    public RestResponse<Integer> api_portal(@PathParam Integer size) {
        return RestResponse.ok(size);
    }

    @GET("csrf")
    public void getCsrfToken(Request request, com.hellokaton.blade.mvc.http.Response response) {
        response.text("token: " + request.attribute("_csrf_token"));
    }

    @GET("exp")
    public String validatorException() {
        return "exp.html";
    }

    @POST("exp")
    public void validatorException(Request request, com.hellokaton.blade.mvc.http.Response response) {
        String name = request.query("name", "");
        Validators.notEmpty().test(name).throwIfInvalid("名称");
        System.out.println("继续执行");
    }

    @POST("upload")
    @Response
    public RestResponse upload(@Multipart FileItem fileItem) throws IOException {
        System.out.println(fileItem);
        fileItem.moveTo(new File(fileItem.getFileName()));
        return RestResponse.ok();
    }

    @POST("save")
    @Response
    public RestResponse savePerson(@Body Map<String, Object> person) {
        return RestResponse.ok(person);
    }

}
