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
package com.blade.task;

import com.blade.task.annotation.Schedule;
import lombok.Data;

import java.lang.reflect.Method;

/**
 * Task struct
 * <p>
 * Used to save task meta information on a method.
 *
 * @author biezhi
 * @date 2018/4/9
 */
@Data
public class TaskStruct {

    private Schedule schedule;
    private Method   method;
    private Class<?> type;

}
