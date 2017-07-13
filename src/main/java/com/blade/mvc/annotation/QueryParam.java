package com.blade.mvc.annotation;

import java.lang.annotation.*;

/**
 * Request Query ParameterAnnotation
 *
 * @author <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.6.6
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface QueryParam {

    boolean required() default false;

    String name() default "";

    String defaultValue() default "";

    /**
     * @return route description
     */
    String description() default "";

}