package com.blade;

import com.blade.event.EventListener;
import com.blade.event.EventType;
import com.blade.mvc.ui.template.TemplateEngine;
import netty_hello.Hello;
import org.junit.Test;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Blade test
 *
 * @author biezhi
 * 2017/6/4
 */
public class BladeTest extends BaseTestCase {

    @Test
    public void testListen() {
        Blade blade = mockBlade();
        blade.listen(9001);
        verify(blade).listen(9001);

        blade.listen("127.0.0.1", 9002);

        verify(blade).listen("127.0.0.1", 9002);
    }

    @Test
    public void testStart() {
        mockBlade().start(Hello.class, null);
    }

    @Test
    public void testAppName() {
        Blade blade = mockBlade();
        blade.appName(anyString());

        verify(blade).appName(anyString());
    }

    @Test
    public void testStartedEvent() {
        Blade         blade    = mockBlade();
        EventListener listener = e1 -> System.out.println("Server started.");

        blade.event(EventType.SERVER_STARTED, listener);

        verify(blade).event(EventType.SERVER_STARTED, listener);
    }

    @Test
    public void testTemplate() {
        Blade          blade          = mockBlade();
        TemplateEngine templateEngine = mock(TemplateEngine.class);

        blade.templateEngine(templateEngine);

        verify(blade).templateEngine(templateEngine);
    }

    @Test
    public void testRegister() {
        Blade  blade  = mockBlade();
        Object object = mock(Object.class);
        blade.register(object);

        verify(blade).register(object);
    }

    @Test
    public void testAddStatics() {
        Blade blade = mockBlade();
        blade.addStatics("/assets/");
        verify(blade).addStatics("/assets/");
    }

    @Test
    public void testEnableCors() {
        Blade blade = mockBlade();
        blade.enableCors(true);

        verify(blade).enableCors(true);
    }
}
