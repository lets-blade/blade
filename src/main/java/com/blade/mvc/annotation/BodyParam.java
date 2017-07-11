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
public @interface BodyParam {

    boolean required() default false;

}