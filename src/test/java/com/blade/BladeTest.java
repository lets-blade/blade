package com.blade;

import com.blade.event.EventType;
import com.blade.mvc.ui.template.DefaultEngine;
import com.blade.types.BladeBeanDefineType;
import netty_hello.Hello;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author biezhi
 * 2017/6/4
 */
public class BladeTest extends BaseTestCase {

    @Test
    public void testListen() {
        app.listen("127.0.0.1", 10086).start().await();
    }

    @Test
    public void testStart() {
        app.start(Hello.class, null);
    }

    @Test
    public void testAppName() {
        start(
                app.appName("bestKill").devMode(false)
        );
    }

    @Test
    public void testStartedEvent() {
        start(
                app.event(EventType.SERVER_STARTED, (e) -> {
                    System.out.println("服务已经启动成功.");
                })
        );
    }

    @Test
    public void testTemplate() {
        start(
                app.templateEngine(new DefaultEngine())
        );
    }

    @Test
    public void testRegister() {
        start(
                app.register(new BladeBeanDefineType())
                        .event(EventType.SERVER_STARTED, e -> {
                            Object bladeBeanDefineType = e.blade.getBean(BladeBeanDefineType.class);
                            Assert.assertNotNull(bladeBeanDefineType);
                        })
        );
    }

    @Test
    public void testAddStatics() {
        start(
                app.addStatics("/assets/").showFileList(true).gzip(true)
        );
    }

    @Test
    public void testEnableCors() {
        start(
                app.enableCors(true)
        );
    }
}
