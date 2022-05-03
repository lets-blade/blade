package com.hellokaton.blade.annotation.route;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PUT {

    /**
     * @return request url
     */
    String[] value() default "/";

    /**
     * @return route description
     */
    String description() default "";
}