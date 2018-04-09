package netty_hello;

import com.blade.ioc.annotation.Bean;
import com.blade.task.TaskContext;
import com.blade.task.annotation.Cron;

import java.time.LocalDateTime;

/**
 * @author biezhi
 * @date 2018/4/9
 */
@Bean
public class TaskModel {

    private int pos = 0;

    @Cron("* * * * * ?")
    public void run1(TaskContext context) {
        if (pos == 5) {
            context.stop();
            return;
        }
        pos++;
        System.out.println(LocalDateTime.now() + ": Hello task. " + Thread.currentThread());
    }

}
