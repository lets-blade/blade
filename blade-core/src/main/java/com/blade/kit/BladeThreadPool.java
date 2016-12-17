package com.blade.kit;

import java.util.concurrent.*;

public class BladeThreadPool {

    public static Executor getExecutor(int threads, int queues) {
        return getThreadPoolExecutor(threads, queues);
    }

    public static ThreadPoolExecutor getThreadPoolExecutor(int threads, int queues) {
        String name = "blade-pool";
        return new ThreadPoolExecutor(threads, threads, 0, TimeUnit.MILLISECONDS,
                queues == 0 ? new SynchronousQueue<Runnable>()
                        : (queues < 0 ? new LinkedBlockingQueue<Runnable>()
                        : new LinkedBlockingQueue<Runnable>(queues)),
                new NamedThreadFactory(name, true), new AbortPolicyWithReport(name));
    }

}