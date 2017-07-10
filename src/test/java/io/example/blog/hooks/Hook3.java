package io.example.blog.hooks;

import com.blade.ioc.annotation.Bean;
import com.blade.ioc.annotation.Order;
import com.blade.mvc.hook.Signature;
import com.blade.mvc.hook.WebHook;

/**
 * @author biezhi
 *         2017/6/2
 */
@Order(3)
@Bean
public class Hook3 implements WebHook {

    @Override
    public boolean before(Signature signature) {
        System.out.println("进入web hook3");
        return signature.next();
    }

}