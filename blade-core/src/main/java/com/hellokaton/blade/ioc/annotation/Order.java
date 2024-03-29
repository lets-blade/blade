package com.hellokaton.blade.ioc.annotation;

import java.lang.annotation.*;

/**
 * Order By
 *
 * @author <a href="mailto:hellokaton@gmail.com" target="_blank">hellokaton</a>
 * @since 1.6.6
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Order {

    int value() default Integer.MAX_VALUE;

}