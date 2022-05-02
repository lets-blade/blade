package com.blade.websocket.annotaion;

import com.blade.ioc.annotation.Bean;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author darren
 * @date 2018-12-10 21:03
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Bean
public @interface WebSocket {

    /**
     * @return websocket path
     */
    String value() default "/websocket";


    /**
     * @return websocket description
     */
    String description() default "";
}
