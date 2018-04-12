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

import com.blade.task.cron.CronExpression;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ScheduledFuture;

import static com.blade.kit.BladeKit.getStartedSymbol;

/**
 * Task
 * <p>
 * Save the name of the task, the code block that is actually executed, the cron expression,
 * and the information that is deferred.
 * <p>
 * It provides a stop method to stop yourself, at this point the task thread will stop and the execution will stop.
 *
 * @author biezhi
 * @date 2018/4/9
 */
@Data
@Slf4j
public class Task {

    private          String             name;
    private          Runnable           task;
    private          ScheduledFuture<?> future;
    private          CronExpression     cronExpression;
    private volatile boolean            isRunning = true;
    private          long               delay;

    public Task(String name, CronExpression cronExpression, long delay) {
        this.name = name;
        this.cronExpression = cronExpression;
        this.delay = delay;
    }

    public boolean stop() {
        if (!isRunning) {
            return true;
        }
        isRunning = false;
        boolean flag = future.cancel(true);
        log.info("{}Task [{}] stoped", getStartedSymbol(), name);
        return flag;
    }

}
