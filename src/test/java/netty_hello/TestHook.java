package netty_hello;

import com.blade.ioc.annotation.Bean;
import com.blade.mvc.RouteContext;
import com.blade.mvc.hook.WebHook;

/**
 * @author biezhi
 * @date 2018/7/3
 */
@Bean
public class TestHook implements WebHook {
    @Override
    public boolean before(RouteContext context) {
        System.out.println("context: " + context);
        return true;
    }
}
