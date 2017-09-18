package com.blade;

import com.blade.event.EventType;
import org.junit.Test;

/**
 * @author biezhi
 *         2017/6/4
 */
public class BladeTest extends BaseTestCase {

    @Test
    public void testAppName() {
        Blade blade = Blade.me();
        blade.appName("demo1");
        blade.start();
    }

    @Test
    public void testStartedEvent() {
        start(
                app.event(EventType.SERVER_STARTED, (e) -> {
                    System.out.println("服务已经启动成功.");
                })
        );
    }

}
