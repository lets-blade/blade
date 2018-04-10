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
package com.blade.task.cron;

import com.blade.task.Task;

import java.util.Date;
import java.util.concurrent.*;

/**
 * CronThreadPoolExecutor
 * <p>
 * the thread pool implementation of the cron expression.
 *
 * @author biezhi
 * @date 2018/4/9
 */
public class CronThreadPoolExecutor extends ScheduledThreadPoolExecutor implements CronExecutorService {

    public CronThreadPoolExecutor(int corePoolSize, ThreadFactory threadFactory) {
        super(corePoolSize, threadFactory);
    }

    @Override
    public ScheduledFuture<?> submit(Task task) {
        if (task == null) throw new NullPointerException();
        CronExpression expression = task.getCronExpression();
        Runnable scheduleTask = () -> {
            Date now  = new Date();
            Date time = expression.getNextValidTimeAfter(now);
            try {
                while (time != null) {
                    if (!task.isRunning()) {
                        break;
                    }
                    CronThreadPoolExecutor.this.schedule(task.getTask(), time.getTime() - now.getTime(), TimeUnit.MILLISECONDS);
                    while (now.before(time)) {
                        Thread.sleep(time.getTime() - now.getTime());
                        now = new Date();
                    }
                    time = expression.getNextValidTimeAfter(now);
                }
            } catch (RejectedExecutionException | CancellationException e) {
                // Occurs if executor was already shutdown when schedule() is called
            } catch (InterruptedException e) {
                // Occurs when executing tasks are interrupted during shutdownNow()
                Thread.currentThread().interrupt();
            }
        };
        return this.schedule(scheduleTask, task.getDelay(), TimeUnit.MILLISECONDS);
    }

}