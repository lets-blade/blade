package com.blade.task;

import com.blade.task.cron.CronExpression;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ScheduledFuture;

import static com.blade.kit.BladeKit.getStartedSymbol;

/**
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
        log.info("{}Task [{}] stoped", getStartedSymbol(), name);
        return future.cancel(true);
    }

}
