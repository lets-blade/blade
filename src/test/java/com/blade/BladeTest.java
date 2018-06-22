package com.blade;

import com.blade.event.EventListener;
import com.blade.event.EventType;
import com.blade.kit.StringKit;
import com.blade.mvc.handler.ExceptionHandler;
import com.blade.mvc.handler.RouteHandler;
import com.blade.mvc.handler.WebSocketHandler;
import com.blade.mvc.hook.WebHook;
import com.blade.mvc.http.HttpSession;
import com.blade.mvc.route.Route;
import com.blade.mvc.ui.template.TemplateEngine;
import com.blade.mvc.websocket.WebSocketContext;
import com.blade.security.web.csrf.CsrfMiddleware;
import com.blade.types.BladeClassDefineType;
import com.mashape.unirest.http.Unirest;
import netty_hello.Hello;
import org.junit.Assert;
import org.junit.Test;

import java.net.ConnectException;
import java.net.Socket;
import java.util.List;

import static com.blade.mvc.Const.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * Blade test
 *
 * @author biezhi
 * 2017/6/4
 */
public class BladeTest extends BaseTestCase {

    private Blade blade = Blade.of();

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
        Blade blade = Blade.of();
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
        Blade blade = Blade.of();
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
        String[] args  = null;
        Blade    start = Blade.of().start(Hello.class, args);
        start.stop();
    }

    @Test
    public void testAppName() {
        Blade  blade     = Blade.of();
        String anyString = StringKit.rand(10);
        blade.appName(anyString);
        assertEquals(anyString, blade.environment().getOrNull(ENV_KEY_APP_NAME));
    }

    @Test
    public void testStartedEvent() {
        Blade         blade    = Blade.of();
        EventListener listener = e1 -> System.out.println("Server started.");
        blade.event(EventType.SERVER_STARTED, listener);
    }

    @Test
    public void testTemplate() {
        Blade          blade          = Blade.of();
        TemplateEngine templateEngine = mock(TemplateEngine.class);
        blade.templateEngine(templateEngine);
        assertEquals(templateEngine, blade.templateEngine());
    }

    @Test
    public void testRegister() {
        Blade                blade  = Blade.of();
        BladeClassDefineType object = new BladeClassDefineType();
        blade.register(object);
        assertEquals(object, blade.ioc().getBean(BladeClassDefineType.class));
    }

    @Test
    public void testAddStatics() {
        Blade blade = Blade.of();
        blade.addStatics("/assets/", "/public");

        assertEquals(7, blade.getStatics().size());
        assertEquals(Boolean.TRUE, blade.getStatics().contains("/assets/"));
        assertEquals(Boolean.FALSE, blade.getStatics().contains("/hello/"));
    }

    @Test
    public void testBootConf() {
        Blade blade = Blade.of();
        String bootConf = blade.bootConf("application2.properties").environment().getOrNull(ENV_KEY_BOOT_CONF);
        assertEquals("application2.properties", bootConf);
    }

    @Test
    public void testEnv() {
        Environment env = Environment.empty();
        env.add("hello", "world");

        Environment environment2 = Blade.of().environment().load(env);

        assertEquals(env, environment2);

        String value = Blade.of().env("blade", "2.0.9");
        assertEquals("2.0.9", value);

        assertEquals(false, Blade.of().env("blade").isPresent());

    }

    @Test
    public void testUse() {
        Blade         blade      = Blade.of().use(new CsrfMiddleware());
        List<WebHook> middleware = blade.middleware();
        Assert.assertNotNull(middleware);
        assertEquals(1, middleware.size());
    }

    @Test
    public void testSessionType() {
        assertEquals(HttpSession.class, Blade.of().sessionType());
        Blade.of().sessionType(HttpSession.class);
    }

    @Test
    public void testOnStarted() {
        Blade.of().onStarted(blade -> System.out.println("On started.."));
    }

    @Test
    public void testDisableSession() {
        Blade blade = Blade.of().disableSession();
        Assert.assertNull(blade.sessionManager());
    }

    @Test
    public void testWatchEnvChange() {
        Environment environment = Blade.of().watchEnvChange(false).environment();
        assertEquals(Boolean.FALSE, environment.getBooleanOrNull(ENV_KEY_APP_WATCH_ENV));
    }

    @Test
    public void testWebSocket() {
        Assert.assertNull(Blade.of().webSocketHandler());
        Blade blade = Blade.of().webSocket("/", new WebSocketHandler() {
            @Override
            public void onConnect(WebSocketContext ctx) {
                System.out.println("on connect.");
            }

            @Override
            public void onText(WebSocketContext ctx) {
                System.out.println("on text");
            }

            @Override
            public void onDisConnect(WebSocketContext ctx) {
                System.out.println("on disconnect.");
            }
        });
        Assert.assertNotNull(blade.webSocketHandler());
    }

    @Test
    public void testBannerText() {
        Blade blade = Blade.of().bannerText("qq");
        assertEquals("qq", blade.bannerText());
    }

    @Test
    public void testThreadName() {
        Blade.of().threadName("-0-");
    }

    @Test
    public void testEnableCors() {
        Blade blade = Blade.of();
        blade.enableCors(true);
        assertEquals(Boolean.TRUE, blade.environment().getBooleanOrNull(ENV_KEY_CORS_ENABLE));
    }

    @Test
    public void testShowFileList() {
        Blade blade = Blade.of();
        blade.showFileList(false);
        assertEquals(Boolean.FALSE, blade.environment().getBooleanOrNull(ENV_KEY_STATIC_LIST));
    }

    @Test
    public void testGZIP() {
        Blade blade = Blade.of();
        blade.gzip(true);
        assertEquals(Boolean.TRUE, blade.environment().getBooleanOrNull(ENV_KEY_GZIP_ENABLE));
    }

    @Test
    public void testGetBean() {
        Blade blade = Blade.of();
        blade.register("hello world");

        String str = blade.getBean(String.class);
        Assert.assertNotNull(str);
        assertEquals("hello world", str);
    }

    @Test
    public void testExceptionHandler() {
        Blade            blade            = Blade.of();
        ExceptionHandler exceptionHandler = mock(ExceptionHandler.class);

        blade.exceptionHandler(exceptionHandler);

        assertEquals(exceptionHandler, blade.exceptionHandler());
    }

    @Test
    public void testDevMode() {
        Blade blade = Blade.of();
        blade.devMode(false);
        assertEquals(Boolean.FALSE, blade.devMode());
    }


}
