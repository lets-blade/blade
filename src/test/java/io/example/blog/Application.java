package io.example.blog;

import com.blade.Blade;
import com.blade.security.web.csrf.CsrfMiddleware;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author biezhi
 *         2017/5/31
 */
public class Application {

    static Map<String, Object> map = new HashMap<>();

    public static void main(String[] args) {

//        System.setProperty("com.blade.logger.defaultLogLevel", "debug");

        map.put("name", "blade");
        map.put("jdk", 1.8);

        Blade.me()
                .devMode(false)
                .use((invoker) -> {
                    System.out.println("hello...");
                    return true;
                }, new CsrfMiddleware())
                .get("/json", ((request, response) -> response.json(map)))
                .get("/t", (request, response) -> sleep())
                .showFileList(true)
                .listen(9001)
                .start(Application.class, args);
    }

    private static void sleep(){
        try {
            TimeUnit.MINUTES.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
