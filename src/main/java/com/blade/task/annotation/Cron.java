package com.blade.task.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Cron expression
 *
 * @author biezhi
 * @date 2018/4/9
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Cron {

    /**
     * cron expression
     *
     * @return
     */
    String value();

    String name() default "";

    /**
     * Delay execution, unit millisecond, start the task by default.
     *
     * @return
     */
    long delay() default 0;

}
