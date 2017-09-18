package com.blade;

import com.blade.mvc.handler.RouteHandler;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.HttpRequest;
import com.mashape.unirest.request.HttpRequestWithBody;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;

/**
 * @author biezhi
 * 2017/6/3
 */
@Slf4j
public class BaseTestCase {

    protected RouteHandler OK_HANDLER = (req, res) -> res.text("OK");
    protected Blade app;
    private   String origin    = "http://127.0.0.1:10086";
    protected String firefoxUA = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.12; rv:53.0) Gecko/20100101 Firefox/53.0";

    @Before
    public void setup() throws Exception {
//        System.setProperty("com.blade.logger.defaultLogLevel", "DEBUG");
        app = Blade.me();
        Unirest.setTimeouts(30_000, 10_000);
    }

    protected Blade start() {
        return Blade.me().listen(10086).start().await();
    }

    protected void start(Blade blade) {
        blade.listen(10086).start().await();
    }

    @After
    public void after() {
        app.stop();
        app.await();
    }

    protected HttpRequest get(String path) throws Exception {
        log.info("[GET] {}", (origin + path));
        return Unirest.get(origin + path);
    }

    protected String getBodyString(String path) throws Exception {
        log.info("[GET] {}", (origin + path));
        return Unirest.get(origin + path).asString().getBody();
    }

    protected HttpRequestWithBody post(String path) throws Exception {
        log.info("[POST] {}", (origin + path));
        return Unirest.post(origin + path);
    }

    protected String postBodyString(String path) throws Exception {
        log.info("[POST] {}", (origin + path));
        return Unirest.post(origin + path).asString().getBody();
    }

    protected HttpRequest put(String path) throws Exception {
        log.info("[PUT] {}", (origin + path));
        return Unirest.put(origin + path);
    }

    protected String putBodyString(String path) throws Exception {
        log.info("[PUT] {}", (origin + path));
        return Unirest.put(origin + path).asString().getBody();
    }

    protected HttpRequest delete(String path) throws Exception {
        log.info("[DELETE] {}", (origin + path));
        return Unirest.delete(origin + path);
    }

    protected String deleteBodyString(String path) throws Exception {
        log.info("[DELETE] {}", (origin + path));
        return Unirest.delete(origin + path).asString().getBody();
    }

    protected String bodyToString(String path) throws Exception {
        return get(path).asString().getBody();
    }

}
