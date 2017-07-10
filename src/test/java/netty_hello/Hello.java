package netty_hello;

import com.blade.Blade;

/**
 * @author biezhi
 *         2017/6/5
 */
public class Hello {

    public static void main(String[] args) {
        Blade.me()
//                .devMode(false)
//                .environment(Const.ENV_KEY_NETTY_WORKERS, Runtime.getRuntime().availableProcessors())
                .get("/hello", ((request, response) -> response.text("Hello World.")))
                .use()
                .start(Hello.class, args);
    }
}
