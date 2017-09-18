package com.blade.mvc;

import com.blade.BaseTestCase;
import com.blade.event.EventType;
import org.junit.Test;

/**
 * @author biezhi
 * 2017/6/5
 */
public class SessionTest extends BaseTestCase {

    @Test
    public void testCreatedEvent() throws Exception {
        start(
                app.get("/", ((request, response) -> request.session()))
                        .event(EventType.SESSION_CREATED, (e) -> {
                            System.out.println("session 创建");
                        })
        );
        bodyToString("/");
    }

}
