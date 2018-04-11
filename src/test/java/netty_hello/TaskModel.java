package netty_hello;

import com.blade.ioc.annotation.Bean;
import com.blade.task.TaskContext;
import com.blade.task.TaskManager;
import com.blade.task.annotation.Schedule;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author biezhi
 * @date 2018/4/9
 */
@Bean
@Slf4j
public class TaskModel {

    private AtomicInteger run1 = new AtomicInteger();
    private AtomicInteger run3 = new AtomicInteger();

    @Schedule(cron = "* * * * * ?")
    public void run1(TaskContext context) {
        if (run1.get() == 5) {
            context.stop();
            return;
        }
        run1.getAndIncrement();
        log.info(LocalDateTime.now() + ": Hello task1. " + Thread.currentThread());
    }

    @Schedule(cron = "* * * * * ?")
    public void run2(TaskContext context) {
        log.info(LocalDateTime.now() + ": Hello task2. " + Thread.currentThread());
    }

    @Schedule(cron = "* * * * * ?", name = "RUN3")
    public void run3() {
        if (run3.get() == 3) {
            TaskManager.stopTask("RUN3");
            return;
        }
        run3.getAndIncrement();
        log.info(LocalDateTime.now() + ": Hello RUN3. " + Thread.currentThread());
    }

}
