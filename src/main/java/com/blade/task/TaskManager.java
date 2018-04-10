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
package com.blade.task;

import com.blade.task.cron.CronExecutorService;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.blade.kit.BladeKit.getStartedSymbol;

/**
 * Task Manager
 * <p>
 * Manages all tasks, including task thread pools and stops, adds, and gets a task.
 *
 * @author biezhi
 * @date 2018/4/9
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TaskManager {

    private final static Map<String, Task> TASK_MAP = new HashMap<>(8);

    private static CronExecutorService cronExecutorService;

    public static void init(CronExecutorService cronExecutorService) {
        if (null != TaskManager.cronExecutorService) {
            throw new RuntimeException("Don't re-initialize the task thread pool.");
        }
        TaskManager.cronExecutorService = cronExecutorService;
        Runtime.getRuntime().addShutdownHook(new Thread(cronExecutorService::shutdown));
    }

    public static CronExecutorService getExecutorService() {
        return cronExecutorService;
    }

    public static void addTask(Task task) {
        TASK_MAP.put(task.getName(), task);
        log.info("{}Add task [{}]", getStartedSymbol(), task.getName());
    }

    public static List<Task> getTasks() {
        return new ArrayList<>(TASK_MAP.values());
    }

    public static Task getTask(String name) {
        return TASK_MAP.get(name);
    }

    public static boolean stopTask(String name) {
        Task task = TASK_MAP.get(name);
        return task.stop();
    }

}
