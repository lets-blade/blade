package com.blade.task.cron;

import java.util.Date;
import java.util.concurrent.*;

/**
 * Scheduled thread-pool executor implementation that leverages a Quartz CronExpression to calculate future execution times for scheduled tasks.
 *
 * @author Paul Ferraro
 * @since 1.1
 */
public class CronThreadPoolExecutor extends ScheduledThreadPoolExecutor implements CronExecutorService {

    public CronThreadPoolExecutor(int corePoolSize) {
        super(corePoolSize);
    }

    /**
     * {@inheritDoc}
     *
     * @see CronExecutorService#schedule(java.lang.Runnable, CronExpression, long)
     */
    @Override
    public ScheduledFuture<?> schedule(final Runnable task, final CronExpression expression, long delay) {
        if (task == null) throw new NullPointerException();
        this.setCorePoolSize(this.getCorePoolSize() + 1);

        Runnable scheduleTask = () -> {
            Date now  = new Date();
            Date time = expression.getNextValidTimeAfter(now);
            try {
                while (time != null) {
                    CronThreadPoolExecutor.this.schedule(task, time.getTime() - now.getTime(), TimeUnit.MILLISECONDS);
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

        return this.schedule(scheduleTask, delay, TimeUnit.MILLISECONDS);
    }

}