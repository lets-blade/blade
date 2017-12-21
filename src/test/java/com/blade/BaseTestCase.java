package com.blade;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.HttpRequest;
import com.mashape.unirest.request.HttpRequestWithBody;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author biezhi
 * 2017/6/3
 */
@Slf4j
public class BaseTestCase {

    protected Blade app;
    private        String origin    = "http://127.0.0.1:10086";
    protected      String firefoxUA = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.12; rv:53.0) Gecko/20100101 Firefox/53.0";
    private static Lock   lock      = new ReentrantLock();

    @Before
    public void setup() throws Exception {
//        System.setProperty("com.blade.logger.defaultLogLevel", "DEBUG");
        app = Blade.me();
        Unirest.setTimeouts(30_000, 10_000);
    }

    protected Blade start() {
        lock.lock();
        Blade blade = null;
        try {
            blade = Blade.me().listen(10086).start().await();
        } finally {
            lock.unlock();
        }
        return blade;
    }

    protected void start(Blade blade) {
        lock.lock();
        try {
            blade.listen(10086).start().await();
        } finally {
            lock.unlock();
        }
    }

    @After
    public void after() {
        try {
            lock.lock();
            app.stop();
            app.await();
        } catch (Exception e) {

        } finally {
            lock.unlock();
        }
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

    protected String putBodyString(String path) throws Exception {
        log.info("[PUT] {}", (origin + path));
        return Unirest.put(origin + path).asString().getBody();
    }

    protected String deleteBodyString(String path) throws Exception {
        log.info("[DELETE] {}", (origin + path));
        return Unirest.delete(origin + path).asString().getBody();
    }

    protected String bodyToString(String path) throws Exception {
        return get(path).asString().getBody();
    }

}
