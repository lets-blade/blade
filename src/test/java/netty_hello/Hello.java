package netty_hello;

import com.blade.Blade;
import com.blade.event.EventType;

/**
 * @author biezhi
 * 2017/6/5
 */
public class Hello {

    public static void main(String[] args) {
        Blade.me()
//                .devMode(false)
//                .environment(Const.ENV_KEY_NETTY_WORKERS, Runtime.getRuntime().availableProcessors())
                .get("/hello", ((request, response) -> response.text("Hello World.")))
                .event(EventType.ENVIRONMENT_CHANGED, new ConfigChanged())
                .start(Hello.class, args);
    }
}
