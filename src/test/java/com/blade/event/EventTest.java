package com.blade.event;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author biezhi
 * @date 2017/9/18
 */
public class EventTest {

    @Test
    public void testEventAndBlade(){
        Event event = new Event(EventType.SERVER_STARTED, "hello");
        Assert.assertEquals("SERVER_STARTED", event.eventType.name());
        Assert.assertNotNull(event.data());
    }

}