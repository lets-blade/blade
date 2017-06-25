package com.blade;

import com.blade.mvc.route.RouteHandler;
import com.github.kevinsawicki.http.HttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;

/**
 * @author biezhi
 *         2017/6/3
 */
@Slf4j
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
        log.info("[GET] {}", (origin + path));
        return HttpRequest.get(origin + path);
    }

    protected HttpRequest post(String path) throws Exception {
        log.info("[POST] {}", (origin + path));
        return HttpRequest.post(origin + path);
    }

    protected HttpRequest put(String path) throws Exception {
        log.info("[PUT] {}", (origin + path));
        return HttpRequest.put(origin + path);
    }

    protected HttpRequest delete(String path) throws Exception {
        log.info("[DELETE] {}", (origin + path));
        return HttpRequest.delete(origin + path);
    }

    protected String bodyToString(String path) throws Exception {
        return get(path).connectTimeout(3000).readTimeout(3000).body();
    }

}
