package io.example.blog.hooks;

import com.blade.ioc.annotation.Bean;
import com.blade.ioc.annotation.Order;
import com.blade.mvc.hook.Invoker;
import com.blade.mvc.hook.WebHook;
import com.blade.mvc.http.Request;

/**
 * @author biezhi
 *         2017/6/2
 */
@Order(2)
@Bean
public class Hook2 implements WebHook {

    @Override
    public boolean before(Invoker invoker) {
        System.out.println("进入web hook2");
        Request request = invoker.request();
        int stop = request.queryInt("stop", 0);
        if (stop == 1) {
            return false;
        }
        return invoker.next();
    }

}