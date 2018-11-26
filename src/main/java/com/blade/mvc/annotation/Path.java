package com.blade.mvc.annotation;

import com.blade.ioc.annotation.Bean;

import java.lang.annotation.*;

/**
 * Route class notes, identifying whether a class is routed
 *
 * @author <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.6.6
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Bean
public @interface Path {

    /**
     * @return namespace
     */
    String value() default "/";

    /**
     * @return route suffix
     */
    String suffix() default "";

    /**
     * @return is restful api
     */
    boolean restful() default false;

    /**
     * @return Whether to create a controller as a singleton, the default is.
     * When false, a new controller instance is created for each request.
     */
    boolean singleton() default true;

    /**
     * @return path description
     */
    String description() default "";
}