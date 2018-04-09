package com.blade.task;

import com.blade.task.cron.CronExecutorService;
import com.blade.task.cron.CronExpression;
import com.blade.task.cron.CronThreadPoolExecutor;

import java.text.ParseException;
import java.time.LocalDateTime;
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

        CronExecutorService cronExecutorService = new CronThreadPoolExecutor(2);
        cronExecutorService.schedule(() -> System.out.println(LocalDateTime.now() + ": " + Thread.currentThread()), new CronExpression("* * * * * ?"));
    }

}