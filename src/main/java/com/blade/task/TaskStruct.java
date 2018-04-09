package com.blade.task;

import com.blade.task.annotation.Cron;
import lombok.Data;

import java.lang.reflect.Method;

/**
 * @author biezhi
 * @date 2018/4/9
 */
@Data
public class TaskStruct {

    private Cron     cron;
    private Method   method;
    private Class<?> type;
    private boolean  isRunning;

}
