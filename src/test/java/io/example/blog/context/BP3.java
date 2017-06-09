package io.example.blog.context;

import com.blade.Blade;
import com.blade.ioc.annotation.Bean;
import com.blade.ioc.annotation.Order;
import com.blade.event.BeanProcessor;

/**
 * @author biezhi
 *         2017/6/1
 */
@Bean
@Order(2)
public class BP3 implements BeanProcessor {

    @Override
    public void processor(Blade blade) {
        System.out.println("bp3 -> " + 2);
    }

}
