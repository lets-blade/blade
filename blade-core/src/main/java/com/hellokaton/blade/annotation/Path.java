package com.hellokaton.blade.annotation;

import com.hellokaton.blade.ioc.annotation.Bean;
import com.hellokaton.blade.mvc.ui.ResponseType;

import java.lang.annotation.*;

/**
 * Route class notes, identifying whether a class is routed
 *
 * @author <a href="mailto:hellokaton@gmail.com" target="_blank">hellokaton</a>
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

    ResponseType responseType() default ResponseType.EMPTY;

    /**
     * @return path description
     */
    String description() default "";
}