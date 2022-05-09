package com.hellokaton.blade;

import com.hellokaton.blade.event.EventListener;
import com.hellokaton.blade.event.EventType;
import com.hellokaton.blade.kit.StringKit;
import com.hellokaton.blade.mvc.handler.ExceptionHandler;
import com.hellokaton.blade.mvc.handler.RouteHandler;
import com.hellokaton.blade.mvc.http.HttpSession;
import com.hellokaton.blade.mvc.ui.template.TemplateEngine;
import com.hellokaton.blade.options.HttpOptions;
import com.hellokaton.blade.options.StaticOptions;
import com.hellokaton.blade.types.BladeClassDefineType;
import com.mashape.unirest.http.Unirest;
import netty_hello.Hello;
import org.junit.Assert;
import org.junit.Test;

import java.net.ConnectException;
import java.net.Socket;

import static com.hellokaton.blade.mvc.BladeConst.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Blade test
 *
 * @author biezhi
 * 2017/6/4
 */
public class BladeTest extends BaseTestCase {

    private Blade blade = Blade.create();

    @Test
    public void testRouteCode() {
        RouteHandler routeHandler = mock(RouteHandler.class);
        blade.get("/", routeHandler);
        blade.post("/", routeHandler);
        blade.delete("/", routeHandler);
        blade.put("/", routeHandler);
        blade.before("/", routeHandler);
        blade.after("/", routeHandler);

        blade.routeMatcher().register();

        Assert.assertNotNull(blade.routeMatcher().lookupRoute("GET", "/"));
        Assert.assertNotNull(blade.routeMatcher().lookupRoute("POST", "/"));
        Assert.assertNotNull(blade.routeMatcher().lookupRoute("DELETE", "/"));
        Assert.assertNotNull(blade.routeMatcher().lookupRoute("PUT", "/"));
        Assert.assertNotNull(blade.routeMatcher().getBefore("/"));
        Assert.assertNotNull(blade.routeMatcher().getAfter("/"));
    }

    @Test
    public void testListen() throws Exception {
        Blade blade = Blade.create();
        blade.listen(9001).start().await();
        try {
            int code = Unirest.get("http://127.0.0.1:9001").asString().getStatus();
            assertEquals(404, code);
        } finally {
            blade.stop();
            try {
                new Socket("127.0.0.1", 9001);
                Assert.fail("Server is still running");
            } catch (ConnectException e) {
            }
        }
    }

    @Test
    public void testListenAddress() throws Exception {
        Blade blade = Blade.create();
        blade.listen("localhost", 9002).start().await();
        try {
            int code = Unirest.get("http://localhost:9002/").asString().getStatus();
            assertEquals(404, code);
        } finally {
            blade.stop();
        }
    }

    @Test
    public void testStart() {
        String[] args = null;
        Blade start = Blade.create().start(Hello.class, args);
        start.stop();
    }

    @Test
    public void testAppName() {
        Blade blade = Blade.create();
        String anyString = StringKit.rand(10);
        assertEquals(anyString, blade.environment().getOrNull(ENV_KEY_APP_NAME));
    }

    @Test
    public void testStartedEvent() {
        Blade blade = Blade.create();
        EventListener listener = e1 -> System.out.println("Server started.");
        blade.event(EventType.SERVER_STARTED, listener);
    }

    @Test
    public void testTemplate() {
        Blade blade = Blade.create();
        TemplateEngine templateEngine = mock(TemplateEngine.class);
        blade.templateEngine(templateEngine);
        assertEquals(templateEngine, blade.templateEngine());
    }

    @Test
    public void testRegister() {
        Blade blade = Blade.create();
        BladeClassDefineType object = new BladeClassDefineType();
        blade.register(object);
        assertEquals(object, blade.ioc().getBean(BladeClassDefineType.class));
    }

    @Test
    public void testAddStatics() {
        Blade blade = Blade.create();
        blade.staticOptions(options -> {
            options.addStatic("/assets/");
            options.addStatic("/assets/");
        });

        assertEquals(7, blade.staticOptions().getPaths().size());
        assertEquals(Boolean.TRUE, blade.staticOptions().getPaths().contains("/assets/"));
        assertEquals(Boolean.FALSE, blade.staticOptions().getPaths().contains("/hello/"));
    }

    @Test
    public void testBootConf() {
        Blade blade = Blade.create();
        String bootConf = blade.bootConf("application2.properties").environment().getOrNull(ENV_KEY_BOOT_CONF);
        assertEquals("application2.properties", bootConf);
    }

    @Test
    public void testEnv() {
        Environment env = Environment.empty();
        env.add("hello", "world");

        Environment environment2 = Blade.create().environment().load(env);

        assertTrue(environment2.hasKey("hello"));

        String value = Blade.create().getEnv("blade", "2.0.9");
        assertEquals("2.0.9", value);

        assertFalse(Blade.create().getEnv("blade").isPresent());
    }

//    @Test
//    public void testUse() {
//        Blade         blade      = Blade.create().use(new CsrfMiddleware());
//        List<WebHook> middleware = blade.middleware();
//        Assert.assertNotNull(middleware);
//        assertEquals(1, middleware.size());
//    }

    @Test
    public void testSessionType() {
        assertEquals(HttpSession.class, Blade.create().httpOptions().getSessionType());
        Blade.create().http(options -> options.setSessionType(HttpSession.class));
    }

    @Test
    public void testOnStarted() {
        Blade.create().addLoader(blade -> System.out.println("On started.."));
    }

    @Test
    public void testDisableSession() {
        Blade blade = Blade.create().disableSession();
        Assert.assertNull(blade.sessionManager());
    }

    @Test
    public void testWatchEnvChange() {
        Environment environment = Blade.create().watchEnvChange(false).environment();
        assertEquals(Boolean.FALSE, environment.getBooleanOrNull(ENV_KEY_APP_WATCH_ENV));
    }

    @Test
    public void testBannerText() {
        Blade blade = Blade.create().bannerText("qq");
        assertEquals("qq", blade.bannerText());
    }

    @Test
    public void testThreadName() {
        Blade.create().threadName("-0-");
    }

    @Test
    public void testShowFileList() {
        Blade blade = Blade.create();
        blade.staticOptions(StaticOptions::showList);
        assertEquals(Boolean.FALSE, blade.staticOptions().isShowList());
    }

    @Test
    public void testGZIP() {
        Blade blade = Blade.create();
        blade.http(HttpOptions::enableGzip);
        assertEquals(Boolean.TRUE, blade.httpOptions().isEnableGzip());
    }

    @Test
    public void testGetBean() {
        Blade blade = Blade.create();
        blade.register("hello world");

        String str = blade.getBean(String.class);
        Assert.assertNotNull(str);
        assertEquals("hello world", str);
    }

    @Test
    public void testExceptionHandler() {
        Blade blade = Blade.create();
        ExceptionHandler exceptionHandler = mock(ExceptionHandler.class);

        blade.exceptionHandler(exceptionHandler);

        assertEquals(exceptionHandler, blade.exceptionHandler());
    }

    @Test
    public void testDevMode() {
        Blade blade = Blade.create();
        blade.devMode(false);
        assertEquals(Boolean.FALSE, blade.devMode());
    }


}
