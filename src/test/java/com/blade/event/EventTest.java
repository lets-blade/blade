package com.blade.event;

import com.blade.Blade;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author biezhi
 * @date 2017/9/18
 */
public class EventTest {

    @Test
    public void testEvent(){
        Event event = new Event(EventType.SERVER_STARTED);
        Assert.assertEquals("SERVER_STARTED", event.eventType.name());
    }

    @Test
    public void testEventAndBlade(){
        Event event = new Event(EventType.SERVER_STARTED, Blade.me());
        Assert.assertEquals("SERVER_STARTED", event.eventType.name());
        Assert.assertNotNull(event.blade);
    }

}