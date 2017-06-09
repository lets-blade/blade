package netty_hello;

import com.blade.Blade;
import com.blade.mvc.Const;

/**
 * @author biezhi
 *         2017/6/5
 */
public class Hello {

    public static void main(String[] args) {

        Blade.me()
                .devMode(false)
                .environment(Const.ENV_KEY_NETTY_WORKERS, Runtime.getRuntime().availableProcessors())
                .get("/rest/hello", ((request, response) -> response.text("Hello World.")))
                .listen(8080).start(args);
    }
}
