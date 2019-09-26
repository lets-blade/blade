package com.blade.ioc.annotation;

import java.lang.annotation.*;

/**
 * Bean annotations can be injected
 *
 * @author <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.5
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Bean {

    String value() default "";

    @Deprecated
    boolean singleton() default true;

}