package com.blade;

import com.blade.mvc.route.RouteHandler;
import com.github.kevinsawicki.http.HttpRequest;
import org.junit.After;
import org.junit.Before;

/**
 * @author biezhi
 *         2017/6/3
 */
public class BaseTestCase {

    protected RouteHandler OK_HANDLER = (req, res) -> res.text("OK");
    protected Blade app;
    private String origin = "http://127.0.0.1:9011";
    protected String firefoxUA = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.12; rv:53.0) Gecko/20100101 Firefox/53.0";

    @Before
    public void setup() throws Exception {
//        System.setProperty("com.blade.logger.defaultLogLevel", "DEBUG");
        app = Blade.me();
    }

    protected void start(Blade blade) {
        blade.listen(9011).start().await();
    }

    @After
    public void after() {
        app.stop();
        app.await();
    }

    protected HttpRequest get(String path) throws Exception {
        return HttpRequest.get(origin + path);
    }

    protected HttpRequest post(String path) throws Exception {
        return HttpRequest.post(origin + path);
    }

    protected HttpRequest put(String path) throws Exception {
        return HttpRequest.put(origin + path);
    }

    protected HttpRequest delete(String path) throws Exception {
        return HttpRequest.delete(origin + path);
    }

    protected String bodyToString(String path) throws Exception {
        return get(path).body();
    }

}
