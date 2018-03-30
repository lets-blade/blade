package netty_hello;

import com.blade.Blade;
import com.blade.event.EventType;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 增强功能
 * @author biezhi
 * 2017/6/5
 */
public class Hello {

    public static void main(String[] args) {
        Blade.me()
//                .devMode(false)
//                .environment(Const.ENV_KEY_NETTY_WORKERS, Runtime.getRuntime().availableProcessors())
                .get("/hello", ((request, response) -> response.text("Hello World.")))
                .get("/error", ((request, response) -> {
                    int a = 1/0;
                    response.text("Hello World.");
                }))
                .post("/hello", ((request, response) -> response.text("Hello World.")))
                .put("/hello", ((request, response) -> response.text("Hello World.")))
                .delete("/hello", ((request, response) -> response.text("Hello World.")))
                .get("/rand", ((request, response) -> {
                    try {
                        TimeUnit.MILLISECONDS.sleep(new Random().nextInt(1000));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    response.success();
                }))
                .event(EventType.ENVIRONMENT_CHANGED, new ConfigChanged())
                .start(Hello.class, args);
    }
}
