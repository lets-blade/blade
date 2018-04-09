package com.blade.task;

import lombok.Data;

/**
 * @author biezhi
 * @date 2018/4/9
 */
@Data
public class TaskContext {

    private Task task;

    public TaskContext(Task task) {
        this.task = task;
    }

    public void stop() {
        task.stop();
    }

}
