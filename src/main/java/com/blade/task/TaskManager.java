package com.blade.task;

import com.blade.task.cron.CronExecutorService;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

import static com.blade.kit.BladeKit.getStartedSymbol;

/**
 * @author biezhi
 * @date 2018/4/9
 */
@Slf4j
public final class TaskManager {

    private final static Map<String, Task>   TASK_MAP = new HashMap<>(8);
    private static       CronExecutorService cronExecutorService;

    public static void init(CronExecutorService cronExecutorService) {
        TaskManager.cronExecutorService = cronExecutorService;
        Runtime.getRuntime().addShutdownHook(new Thread(cronExecutorService::shutdown));
    }

    public static void addTask(Task task) {
        TASK_MAP.put(task.getName(), task);
        log.info("{}Add task [{}]", getStartedSymbol(), task.getName());
    }

    public static Task getTask(String name) {
        return TASK_MAP.get(name);
    }

    public static boolean stopTask(String name) {
        Task task = TASK_MAP.get(name);
        return task.stop();
    }

}
