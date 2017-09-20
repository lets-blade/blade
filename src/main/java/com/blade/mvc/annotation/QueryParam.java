package com.blade.mvc.annotation;

import java.lang.annotation.*;

/**
 * Request Query ParmeterAnnotation
 *
 * @see Param
 *
 * @author <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 2.0.1-beta
 */
@Deprecated
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface QueryParam {

    String name() default "";

    String defaultValue() default "";

    /**
     * @return route description
     */
    String description() default "";

}