package io.example.blog.service;

import com.blade.ioc.annotation.Bean;

/**
 * @author biezhi
 *         2017/6/2
 */
@Bean
public class AService {

    public void sayHi() {
        System.out.println("hi a service");
    }

    public void exp() {
        throw new RuntimeException("业务异常");
    }
}
