package com.hellokaton.blade.annotation.route;

import com.hellokaton.blade.mvc.ui.ResponseType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface POST {

    String[] value() default "/";

    ResponseType responseType() default ResponseType.EMPTY;

    /**
     * @return route description
     */
    String description() default "";
}