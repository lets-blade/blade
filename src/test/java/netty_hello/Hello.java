package netty_hello;

import com.blade.Blade;
import com.blade.event.EventType;
import com.blade.mvc.http.EmptyBody;
import com.blade.mvc.http.ByteBody;
import com.blade.mvc.http.StreamBody;

import java.io.*;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author biezhi
 * 2017/6/5
 */
public class Hello {

    public static void main(String[] args) {

        Blade.of()
//                .devMode(false)
//                .environment(Const.ENV_KEY_NETTY_WORKERS, Runtime.getRuntime().availableProcessors())
                .get("/", ctx -> {
                    String[] chars = new String[]{"Here a special char \" that not escaped", "And Another \\ char"};
                    ctx.json(chars);
                })
                .get("/d1", ctx -> {
                    File file = new File("/Users/biezhi/Pictures/rand/003.jpg");
                    ctx.response().contentType("image/jpeg");
                    ctx.response().header("Content-Disposition", "attachment; filename=003.jpg");
                    ctx.response().body(ByteBody.of(file));
                })
                .get("/d2", ctx -> {
                    File file = new File("/Users/biezhi/Pictures/rand/003.jpg");
                    try (FileInputStream inputStream = new FileInputStream(file)) {
                        ctx.response().contentType("image/jpef");
                        ctx.response().header("Content-Disposition", "attachment; filename=m1.png");
                        ctx.response().body(StreamBody.of(inputStream));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                })
                .get("/d3", ctx -> {
                    String str = "hello world";
                    ctx.response().contentType("text/html");
                    ctx.response().body(ByteBody.of(str.getBytes()));
                })
                .get("/hello", ctx -> ctx.text("Hello World."))
                .get("/error", ctx -> {
                    int a = 1 / 0;
                    ctx.text("Hello World.");
                })
                .post("/hello", ctx -> ctx.text("Hello World."))
                .put("/hello", ctx -> ctx.text("Hello World."))
                .delete("/hello", ctx -> ctx.text("Hello World."))
                .get("/download", ctx -> {
                    try {
                        ctx.response().download("hello.txt", new File("/Users/biezhi/workspace/projects/java/blade/src/test/resources/static/a.txt"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
//                .before("/*", ctx -> {
//                    System.out.println("Before...");
//                })
                .get("/rand", ctx -> {
                    try {
                        TimeUnit.MILLISECONDS.sleep(new Random().nextInt(1000));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    ctx.body(EmptyBody.empty());
                })
//                .use(new XssMiddleware())
//                .use(new CsrfMiddleware())
                .event(EventType.ENVIRONMENT_CHANGED, new ConfigChanged())
                .event(EventType.SESSION_DESTROY, e -> {
                    System.out.println("session 失效了");
                })
                .disableSession()
                .disableCost()
//                .showFileList(true)
//                .gzip(true)
//                .enableCors(true)
                .start(Hello.class, args);
    }

}
