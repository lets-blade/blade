package com.hellokaton.blade.task;

import com.hellokaton.blade.task.cron.CronExecutorService;
import com.hellokaton.blade.task.cron.CronExpression;
import com.hellokaton.blade.task.cron.CronThreadPoolExecutor;

import java.text.ParseException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author biezhi
 * @date 2018/4/9
 */
public class TaskTest {

    public static void main(String[] args) throws ParseException {
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(2);
        System.out.println("Hello World1");
        scheduledThreadPoolExecutor.schedule(() -> {
            System.out.println("Hello World2");
        }, 5000, TimeUnit.MILLISECONDS);

        scheduledThreadPoolExecutor.shutdown();

        CronExecutorService cronExecutorService = new CronThreadPoolExecutor(2, null);
        cronExecutorService.submit(new Task("task1", new CronExpression("* * * * ?"), 0L));
    }

}