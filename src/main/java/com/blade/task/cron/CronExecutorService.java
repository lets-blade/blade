package com.blade.task.cron;

import com.blade.task.Task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledFuture;

/**
 * Executor service that schedules a runnable task for execution via a cron expression.
 *
 * @author Paul Ferraro
 */
public interface CronExecutorService extends ExecutorService {

    /**
     * Schedules the specified task to execute according to the specified cron expression.
     *
     * @param task       the Runnable task to schedule
     * @param expression a cron expression
     */
    ScheduledFuture<?> schedule(Runnable task, CronExpression expression, long delay);

    ScheduledFuture<?> submit(Task task);

    default ScheduledFuture<?> schedule(Runnable task, CronExpression expression) {
        return this.schedule(task, expression, 0L);
    }

}