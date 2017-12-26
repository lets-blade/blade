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
import static org.mockito.Mockito.mock;

/**
 * Blade test
 *
 * @author biezhi
 * 2017/6/4
 */
public class BladeTest extends BaseTestCase {

    @Test
    public void testRouteCode() throws Exception {
        Blade blade = Blade.me();

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
        Blade blade = Blade.me();
        blade.listen(9001).start().await();
        try {
            int code = Unirest.get("http://127.0.0.1:9001").asString().getStatus();
            Assert.assertEquals(404, code);
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
        Blade blade = Blade.me();
        blade.listen("localhost", 9002).start().await();
        try {
            int code = Unirest.get("http://localhost:9002/").asString().getStatus();
            Assert.assertEquals(404, code);
        } finally {
            blade.stop();
            try {
                new Socket("localhost", 9002);
                Assert.fail("Server is still running");
            } catch (ConnectException e) {
            }
        }
    }

    @Test
    public void testStart() {
        Blade.me().start(Hello.class, null);
    }

    @Test
    public void testAppName() {
        Blade  blade     = Blade.me();
        String anyString = StringKit.rand(10);
        blade.appName(anyString);
        Assert.assertEquals(anyString, blade.environment().getOrNull(ENV_KEY_APP_NAME));
    }

    @Test
    public void testStartedEvent() {
        Blade         blade    = Blade.me();
        EventListener listener = e1 -> System.out.println("Server started.");
        blade.event(EventType.SERVER_STARTED, listener);
    }

    @Test
    public void testTemplate() {
        Blade          blade          = Blade.me();
        TemplateEngine templateEngine = mock(TemplateEngine.class);
        blade.templateEngine(templateEngine);
        Assert.assertEquals(templateEngine, blade.templateEngine());
    }

    @Test
    public void testRegister() {
        Blade                blade  = Blade.me();
        BladeClassDefineType object = new BladeClassDefineType();
        blade.register(object);
        Assert.assertEquals(object, blade.ioc().getBean(BladeClassDefineType.class));
    }

    @Test
    public void testAddStatics() {
        Blade blade = Blade.me();
        blade.addStatics("/assets/", "/public");

        Assert.assertEquals(7, blade.getStatics().size());
        Assert.assertEquals(Boolean.TRUE, blade.getStatics().contains("/assets/"));
        Assert.assertEquals(Boolean.FALSE, blade.getStatics().contains("/hello/"));
    }

    @Test
    public void testBootConf(){
        Blade blade = Blade.me();
        blade.bootConf("app2.properties");
        Assert.assertEquals("app2.properties", blade.environment().getOrNull(ENV_KEY_BOOT_CONF));
    }

    @Test
    public void testEnv(){
        Environment environment = Environment.empty();
        environment.add("hello", "world");
        Environment environment2 = Blade.me().environment(environment).environment();
        Assert.assertEquals(environment, environment2);
    }

    @Test
    public void testUse(){
        Blade         blade      = Blade.me().use(new CsrfMiddleware());
        List<WebHook> middleware = blade.middleware();
        Assert.assertNotNull(middleware);
        Assert.assertEquals(1, middleware.size());
    }

    @Test
    public void testSessionType(){
        Assert.assertEquals(HttpSession.class, Blade.me().sessionType());
        Blade.me().sessionType(HttpSession.class);
    }

    @Test
    public void testOnStarted(){
        Blade.me().onStarted(blade -> System.out.println("On started.."));
    }

    @Test
    public void testDisableSession(){
        Blade blade = Blade.me().disableSession();
        Assert.assertNull(blade.sessionManager());
    }

    @Test
    public void testWatchEnvChange(){
         Environment environment = Blade.me().watchEnvChange(false).environment();
         Assert.assertEquals(Boolean.FALSE, environment.getBooleanOrNull(ENV_KEY_APP_WATCH_ENV));
    }

    @Test
    public void testWebSocket(){
        Assert.assertNull(Blade.me().webSocketHandler());
        Blade blade = Blade.me().webSocket("/", new WebSocketHandler() {
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
    public void testBannerText(){
        Blade blade = Blade.me().bannerText("qq");
        Assert.assertEquals("qq", blade.bannerText());
    }

    @Test
    public void testThreadName(){
        Blade.me().threadName("-0-");
    }

    @Test
    public void testEnableCors() {
        Blade blade = Blade.me();
        blade.enableCors(true);
        Assert.assertEquals(Boolean.TRUE, blade.environment().getBooleanOrNull(ENV_KEY_CORS_ENABLE));
    }

    @Test
    public void testShowFileList() {
        Blade blade = Blade.me();
        blade.showFileList(false);
        Assert.assertEquals(Boolean.FALSE, blade.environment().getBooleanOrNull(ENV_KEY_STATIC_LIST));
    }

    @Test
    public void testGZIP() {
        Blade blade = Blade.me();
        blade.gzip(true);
        Assert.assertEquals(Boolean.TRUE, blade.environment().getBooleanOrNull(ENV_KEY_GZIP_ENABLE));
    }

    @Test
    public void testGetBean() {
        Blade blade = Blade.me();
        blade.register("hello world");

        String str = blade.getBean(String.class);
        Assert.assertNotNull(str);
        Assert.assertEquals("hello world", str);
    }

    @Test
    public void testExceptionHandler() {
        Blade            blade            = Blade.me();
        ExceptionHandler exceptionHandler = mock(ExceptionHandler.class);

        blade.exceptionHandler(exceptionHandler);

        Assert.assertEquals(exceptionHandler, blade.exceptionHandler());
    }

    @Test
    public void testDevMode() {
        Blade blade = Blade.me();
        blade.devMode(false);
        Assert.assertEquals(Boolean.FALSE, blade.devMode());
    }


}
