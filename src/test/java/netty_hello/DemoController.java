package netty_hello;

import com.blade.mvc.annotation.GetRoute;
import com.blade.mvc.annotation.Param;
import com.blade.mvc.annotation.Path;
import com.blade.mvc.annotation.Route;
import com.blade.mvc.http.Request;

/**
 *
 * @author biezhi
 * @date 2018/4/18
 */
@Path
public class DemoController {

    @GetRoute("p")
    public void p(@Param String p1){
        System.out.println(p1);
    }

    @Route("hi/:a/:b/:c")
    public void pathParam(Request request){
        System.out.println(request.pathString("a"));
        System.out.println(request.pathString("b"));
        System.out.println(request.pathString("c"));
    }

}
