package com.blade.task;

import java.util.concurrent.CountDownLatch;
import java.util.stream.IntStream;

/**
 * @author PSH
 * @date 2019/03/16
 */
public class TaskManagerTest {
    public static void main(String[] args) {
        CountDownLatch downLatch = new CountDownLatch(500);
        IntStream.range(0,500).forEach(i -> {
            Task task = new Task("task-" + i, null, Integer.MAX_VALUE);
            new Thread(()->{
                TaskManager.addTask(task);
                downLatch.countDown();
            }).start();
        });
        try {
            downLatch.await();
            System.out.println("TaskManager.getTasks().size() = " + TaskManager.getTasks().size());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
