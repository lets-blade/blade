package com.blade.annotation.request;

import java.lang.annotation.*;

/**
 * Request Body Annotation
 *
 * @author <a href="mailto:hellokaton@gmail.com" target="_blank">hellokaton</a>
 * @since 2.1.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Body {

    boolean required() default false;

}