/**
 * Copyright (c) 2017, biezhi 王爵 (biezhi.me@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

    /**
     * The name of this task, when you don't specify it, will use the "task-0" index to start with 0,
     * and the order can be messy.
     * <p>
     * If you want to manually manipulate a task, suggest specifying the name.
     *
     * @return
     */
    String name() default "";

    /**
     * Delay execution, unit millisecond, start the task by default.
     *
     * @return returns the number of milliseconds to delay execution.
     */
    long delay() default 0;

}
