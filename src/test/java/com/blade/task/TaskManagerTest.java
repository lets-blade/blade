package com.blade.task;

import java.util.concurrent.CountDownLatch;
import java.util.stream.IntStream;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author PSH
 * @date 2019/03/16
 */
public class TaskManagerTest {

    @Test
    public void testAddTaskMultiThreading() throws Exception {

        final int tackCount = 500;
        CountDownLatch downLatch = new CountDownLatch(tackCount);
        IntStream.range(0, tackCount).forEach(i -> {
            Task task = new Task("task-" + i, null, Integer.MAX_VALUE);
            new Thread(() -> {
                TaskManager.addTask(task);
                downLatch.countDown();
            }).start();
        });

        downLatch.await();
        Assert.assertEquals(tackCount, TaskManager.getTasks().size());
    }
}
