/**
 * Copyright (c) 2016, biezhi 王爵 (biezhi.me@gmail.com)
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
package com.blade.kit.thread;

import java.util.concurrent.*;

public final class ThreadPoolKit {

    private ThreadPoolKit(){}

    public static Executor getExecutor(int threads, int queues) {
        return getThreadPoolExecutor("DEF-POOL", threads, queues);
    }

    public static Executor getExecutor(String poolName, int threads, int queues) {
        return getThreadPoolExecutor(poolName, threads, queues);
    }

    public static ThreadPoolExecutor getThreadPoolExecutor(String poolName, int threads, int queues) {
        return new ThreadPoolExecutor(threads, threads, 0, TimeUnit.MILLISECONDS,
                queues == 0 ? new SynchronousQueue<>()
                        : (queues < 0 ? new LinkedBlockingQueue<>()
                        : new LinkedBlockingQueue<>(queues)),
                new NamedThreadFactory(poolName, true), new AbortPolicyWithReport(poolName));
    }

}