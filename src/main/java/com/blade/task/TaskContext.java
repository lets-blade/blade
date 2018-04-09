package com.blade.task;

import com.blade.task.cron.CronExpression;
import lombok.Setter;

import java.util.concurrent.ScheduledFuture;

/**
 * @author biezhi
 * @date 2018/4/9
 */
public class TaskContext {

    @Setter
    private ScheduledFuture<?> future;

    public void stop() {
        if (null != future) {
            future.cancel(true);
        }
    }

}
