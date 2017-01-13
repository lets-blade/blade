/**
 * Copyright (c) 2015, biezhi 王爵 (biezhi.me@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blade.kit;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 定时任务
 * 
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public abstract class TaskKit {

    private static Logger logger = LoggerFactory.getLogger(TaskKit.class);

    private static ScheduledThreadPoolExecutor taskScheduler = new ScheduledThreadPoolExecutor(getBestPoolSize());
    private static List<Timer> timerList = new ArrayList<Timer>();

    /**
     * 立即启动，并以固定的频率来运行任务。后续任务的启动时间不受前次任务延时影响。
     * @param task 具体待执行的任务
     * @param periodSeconds 每次执行任务的间隔时间(单位秒)
     */
    public static ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long periodSeconds) {
        return scheduleAtFixedRate(task, 0, periodSeconds, TimeUnit.SECONDS);
    }

    /**
     * 在指定的延时之后开始以固定的频率来运行任务。后续任务的启动时间不受前次任务延时影响。
     * @param task 具体待执行的任务
     * @param initialDelay 首次执行任务的延时时间
     * @param period 每次执行任务的间隔时间(单位秒)
     * @param unit 时间单位
     */
    public static ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long initialDelay, long period, TimeUnit unit) {
        return taskScheduler.scheduleAtFixedRate(task, initialDelay, period, unit);
    }

    /**
     * 在指定的时间点开始以固定的频率运行任务。后续任务的启动时间不受前次任务延时影响。
     * @param task 具体待执行的任务
     * @param startTime 首次运行的时间点,支持 "yyyy-MM-dd HH:mm:ss" 格式
     * @param period 每次执行任务的间隔时间
     * @param unit 时间单位
     */
    public static void scheduleAtFixedRate(Runnable task, String startTime, long period, TimeUnit unit) throws ParseException {
        Date dt = DateKit.dateFormat(startTime, "yyyy-MM-dd HH:mm:ss");
        scheduleAtFixedRate(task, dt, period, unit);
    }

    /**
     * 在指定的时间点开始以固定的频率运行任务。后续任务的启动时间不受前次任务延时影响。
     * @param task 具体待执行的任务
     * @param startTime 首次运行的时间点
     * @param period 每次执行任务的间隔时间
     * @param unit 时间单位
     */
    public static void scheduleAtFixedRate(final Runnable task, Date startTime, final long period, final TimeUnit unit) {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                taskScheduler.scheduleAtFixedRate(task, 0, period, unit);
                timer.cancel();
                timerList.remove(timer);
            }
        }, startTime);
        timerList.add(timer);
    }

    /**
     * 立即启动，两次任务间保持固定的时间间隔
     * @param task 具体待执行的任务
     * @param periodSeconds 两次任务的间隔时间(单位秒)
     */
    public static ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, long periodSeconds) {
        return scheduleWithFixedDelay(task, 0, periodSeconds, TimeUnit.SECONDS);
    }

    /**
     * 在指定的延时之后启动，两次任务间保持固定的时间间隔
     * @param task 具体待执行的任务
     * @param initialDelay 首次执行任务的延时时间
     * @param period 两次任务的间隔时间(单位秒)
     * @param unit 时间单位
     */
    public static ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, long initialDelay, long period, TimeUnit unit) {
        return taskScheduler.scheduleWithFixedDelay(task, initialDelay, period, unit);
    }

    /**
     * 在指定的时间点启动，两次任务间保持固定的时间间隔
     * @param task 具体待执行的任务
     * @param startTime 首次运行的时间点,支持 "yyyy-MM-dd HH:mm:ss" 格式
     * @param period 两次任务的间隔时间
     * @param unit 时间单位
     */
    public static void scheduleWithFixedDelay(Runnable task, String startTime, long period, TimeUnit unit) throws ParseException {
        Date dt = DateKit.dateFormat(startTime, "yyyy-MM-dd HH:mm:ss");
        scheduleWithFixedDelay(task, dt, period, unit);
    }

    /**
     * 在指定的时间点启动，两次任务间保持固定的时间间隔
     * @param task 具体待执行的任务
     * @param startTime 首次运行的时间点
     * @param period 两次任务的间隔时间
     * @param unit 时间单位
     */
    public static void scheduleWithFixedDelay(final Runnable task, Date startTime, final long period, final TimeUnit unit) {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                taskScheduler.scheduleWithFixedDelay(task, 0, period, unit);
                timer.cancel();
                timerList.remove(timer);
            }
        }, startTime);
        timerList.add(timer);
    }

    /**
     * 调整线程池大小
     * @param threadPoolSize
     */
    public static void resizeThreadPool(int threadPoolSize) {
        taskScheduler.setCorePoolSize(threadPoolSize);
    }

    /**
     * 返回定时任务线程池，可做更高级的应用
     * @return
     */
    public static ScheduledThreadPoolExecutor getTaskScheduler() {
        return taskScheduler;
    }

    /**
     * 关闭定时任务服务
     * <p>系统关闭时可调用此方法终止正在执行的定时任务，一旦关闭后不允许再向线程池中添加任务，否则会报RejectedExecutionException异常</p>
     */
    public static void depose() {
    	int timerNum = timerList.size();
    	//清除Timer
    	synchronized (timerList) {
    		for (Timer t: timerList)
    			t.cancel();
    		timerList.clear();
    	}
    	
        List<Runnable> awaitingExecution = taskScheduler.shutdownNow();
        logger.info("Tasks stopping. Tasks awaiting execution: {}", timerNum + awaitingExecution.size());
    }

    /**
     * 重启动定时任务服务
     */
    public static void reset() {
        depose();
        taskScheduler = new ScheduledThreadPoolExecutor(getBestPoolSize());
    }

    /**
     * 根据 Java 虚拟机可用处理器数目返回最佳的线程数。<br>
     * 最佳的线程数 = CPU可用核心数 / (1 - 阻塞系数)，其中阻塞系数这里设为0.9
     */
    private static int getBestPoolSize() {
        try {
            // JVM可用处理器的个数
            final int cores = Runtime.getRuntime().availableProcessors();
            // 最佳的线程数 = CPU可用核心数 / (1 - 阻塞系数)
            // TODO 阻塞系数是不是需要有个setter方法能让使用者自由设置呢？
            return (int)(cores / (1 - 0.9));
        }
        catch (Throwable e) {
            // 异常发生时姑且返回10个任务线程池
            return 10;
        }
    }
}
