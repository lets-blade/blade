package com.blade.mvc.annotation;

import com.blade.ioc.annotation.Bean;

import java.lang.annotation.*;

/**
 * WebHook url pattern
 *
 * @author <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 2.0.6-Alpha1
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Bean
public @interface URLPattern {

    /**
     * @return URL patterns
     */
    String[] values() default {"/*"};

}