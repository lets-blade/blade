package com.blade.annotation.request;

import java.lang.annotation.*;

/**
 * Request Header ParmeterAnnotation
 *
 * @author <a href="mailto:hellokaton@gmail.com" target="_blank">hellokaton</a>
 * @since 1.6.6
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Header {

    String value() default "";

    String defaultValue() default "";

}