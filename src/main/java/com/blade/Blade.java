/**
 * Copyright (c) 2017, biezhi 王爵 (biezhi.me@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blade;

import com.blade.event.BeanProcessor;
import com.blade.event.EventListener;
import com.blade.event.EventManager;
import com.blade.event.EventType;
import com.blade.ioc.Ioc;
import com.blade.ioc.SimpleIoc;
import com.blade.kit.Assert;
import com.blade.kit.BladeKit;
import com.blade.mvc.SessionManager;
import com.blade.mvc.handler.DefaultExceptionHandler;
import com.blade.mvc.handler.ExceptionHandler;
import com.blade.mvc.handler.RouteHandler;
import com.blade.mvc.hook.WebHook;
import com.blade.mvc.http.HttpMethod;
import com.blade.mvc.http.HttpSession;
import com.blade.mvc.http.Session;
import com.blade.mvc.route.RouteMatcher;
import com.blade.mvc.ui.template.DefaultEngine;
import com.blade.mvc.ui.template.TemplateEngine;
import com.blade.server.netty.NettyServer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

import static com.blade.mvc.Const.*;

/**
 * Blade Core
 *
 * @author biezhi 2017/5/31
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Blade {

    private List<WebHook>            middleware              = new ArrayList<>();
    private List<BeanProcessor>      processors              = new ArrayList<>();
    private Set<String>              packages                = new LinkedHashSet<>(PLUGIN_PACKAGE_NAME);
    private Set<String>              statics                 = new HashSet<>(DEFAULT_STATICS);
    private Ioc                      ioc                     = new SimpleIoc();
    private TemplateEngine           templateEngine          = new DefaultEngine();
    private EventManager             eventManager            = new EventManager();
    private SessionManager           sessionManager          = new SessionManager();
    private CountDownLatch           latch                   = new CountDownLatch(1);
    private NettyServer              nettyServer             = new NettyServer();
    private RouteMatcher             routeMatcher            = new RouteMatcher();
    private Environment              environment             = Environment.empty();
    private Consumer<Exception>      startupExceptionHandler = (e) -> log.error("Start blade failed", e);
    private ExceptionHandler         exceptionHandler        = new DefaultExceptionHandler();
    private boolean                  started                 = false;
    private Class<?>                 bootClass               = null;
    private Class<? extends Session> sessionImplType         = HttpSession.class;

    /**
     * Give your blade instance
     *
     * @return return blade instance
     */
    public static Blade me() {
        return new Blade();
    }

    /**
     * Get blade ioc container, default is SimpleIoc implement
     *
     * @return return ioc container
     */
    public Ioc ioc() {
        return ioc;
    }

    /**
     * Add a get route to routes
     *
     * @param path    your route path
     * @param handler route implement
     * @return return blade instance
     */
    public Blade get(@NonNull String path, @NonNull RouteHandler handler) {
        routeMatcher.addRoute(path, handler, HttpMethod.GET);
        return this;
    }

    /**
     * Add a post route to routes
     *
     * @param path    your route path
     * @param handler route implement
     * @return return blade instance
     */
    public Blade post(@NonNull String path, @NonNull RouteHandler handler) {
        routeMatcher.addRoute(path, handler, HttpMethod.POST);
        return this;
    }

    /**
     * Add a put route to routes
     *
     * @param path    your route path
     * @param handler route implement
     * @return return blade instance
     */
    public Blade put(@NonNull String path, @NonNull RouteHandler handler) {
        routeMatcher.addRoute(path, handler, HttpMethod.PUT);
        return this;
    }

    /**
     * Add a delete route to routes
     *
     * @param path    your route path
     * @param handler route implement
     * @return return blade instance
     */
    public Blade delete(@NonNull String path, @NonNull RouteHandler handler) {
        routeMatcher.addRoute(path, handler, HttpMethod.DELETE);
        return this;
    }

    /**
     * Add a before route to routes, the before route will be executed before matching route
     *
     * @param path    your route path
     * @param handler route implement
     * @return return blade instance
     */
    public Blade before(@NonNull String path, @NonNull RouteHandler handler) {
        routeMatcher.addRoute(path, handler, HttpMethod.BEFORE);
        return this;
    }

    /**
     * Add a after route to routes, the before route will be executed after matching route
     *
     * @param path    your route path
     * @param handler route implement
     * @return return blade instance
     */
    public Blade after(@NonNull String path, @NonNull RouteHandler handler) {
        routeMatcher.addRoute(path, handler, HttpMethod.AFTER);
        return this;
    }

    /**
     * Setting blade mvc default templateEngine
     *
     * @param templateEngine TemplateEngine object
     * @return blade
     */
    public Blade templateEngine(@NonNull TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
        return this;
    }

    /**
     * Get TemplateEngine, default is DefaultEngine
     *
     * @return return TemplateEngine
     */
    public TemplateEngine templateEngine() {
        return templateEngine;
    }

    /**
     * Get RouteMatcher
     *
     * @return return RouteMatcher
     */
    public RouteMatcher routeMatcher() {
        return routeMatcher;
    }

    /**
     * Register bean to ioc container
     *
     * @param bean bean object
     * @return blade
     */
    public Blade register(@NonNull Object bean) {
        ioc.addBean(bean);
        return this;
    }

    /**
     * Register bean to ioc container
     *
     * @param cls bean class, the class must provide a no args constructor
     * @return blade
     */
    public Blade register(@NonNull Class<?> cls) {
        ioc.addBean(cls);
        return this;
    }

    /**
     * Add multiple static resource file
     * the default provides the static, upload
     *
     * @param folders static resource directory
     * @return blade
     */
    public Blade addStatics(@NonNull String... folders) {
        statics.addAll(Arrays.asList(folders));
        return this;
    }

    /**
     * Set whether to show the file directory, default doesn't show
     *
     * @param fileList show the file directory
     * @return blade
     */
    public Blade showFileList(boolean fileList) {
        this.environment(ENV_KEY_STATIC_LIST, fileList);
        return this;
    }

    /**
     * Set whether open gzip, default disabled
     *
     * @param gzipEnable enabled gzip
     * @return blade
     */
    public Blade gzip(boolean gzipEnable) {
        this.environment(ENV_KEY_GZIP_ENABLE, gzipEnable);
        return this;
    }

    /**
     * Get ioc bean
     *
     * @param cls bean class type
     * @return return bean instance
     */
    public Object getBean(@NonNull Class<?> cls) {
        return ioc.getBean(cls);
    }

    /**
     * Get ExceptionHandler
     *
     * @return return ExceptionHandler
     */
    public ExceptionHandler exceptionHandler() {
        return exceptionHandler;
    }

    /**
     * Set ExceptionHandler, when you need a custom exception handling
     *
     * @param exceptionHandler your ExceptionHandler instance
     * @return return blade instance
     */
    public Blade exceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        return this;
    }

    /**
     * Get current is developer mode
     *
     * @return return true is developer mode, else not.
     */
    public boolean devMode() {
        return environment.getBoolean(ENV_KEY_DEV_MODE, true);
    }

    /**
     * Whether encoding setting mode for developers
     * The default mode is developers
     *
     * @param devMode developer mode
     * @return blade
     */
    public Blade devMode(boolean devMode) {
        this.environment(ENV_KEY_DEV_MODE, devMode);
        return this;
    }

    public Class<?> bootClass() {
        return this.bootClass;
    }

    /**
     * Set whether to enable cors
     *
     * @param enableCors enable cors
     * @return blade
     */
    public Blade enableCors(boolean enableCors) {
        this.environment(ENV_KEY_CORS_ENABLE, enableCors);
        return this;
    }

    /**
     * Get blade statics list.
     * e.g: "/favicon.ico", "/robots.txt", "/static/", "/upload/", "/webjars/"
     *
     * @return return statics
     */
    public Set<String> getStatics() {
        return statics;
    }

    /**
     * When set to start blade scan packages
     *
     * @param packages package name
     * @return blade
     */
    public Blade scanPackages(@NonNull String... packages) {
        this.packages.addAll(Arrays.asList(packages));
        return this;
    }

    /**
     * Get scan the package set.
     *
     * @return return packages set
     */
    public Set<String> scanPackages() {
        return packages;
    }

    /**
     * Set to start blade configuration file by default
     * Boot config properties file in classpath directory.
     * <p>
     * Without setting will read the classpath -> app.properties
     *
     * @param bootConf boot config file name
     * @return blade
     */
    public Blade bootConf(@NonNull String bootConf) {
        this.environment(ENV_KEY_BOOT_CONF, bootConf);
        return this;
    }

    /**
     * Set the environment variable for global use here
     *
     * @param key   environment key
     * @param value environment value
     * @return blade
     */
    public Blade environment(@NonNull String key, @NonNull Object value) {
        environment.set(key, value);
        return this;
    }

    public Environment environment() {
        return environment;
    }

    /**
     * Set to start the web server to monitor port, the default is 9000
     *
     * @param port web server port
     * @return blade
     */
    public Blade listen(int port) {
        Assert.greaterThan(port, 0, "server port not is negative number.");
        this.environment(ENV_KEY_SERVER_PORT, port);
        return this;
    }

    /**
     * Set to start the web server to listen the IP address and port
     * The default will listen 0.0.0.0:9000
     *
     * @param address ip address
     * @param port    web server port
     * @return blade
     */
    public Blade listen(@NonNull String address, int port) {
        Assert.greaterThan(port, 0, "server port not is negative number.");
        this.environment(ENV_KEY_SERVER_ADDRESS, address);
        this.environment(ENV_KEY_SERVER_PORT, port);
        return this;
    }

    /**
     * The use of multiple middleware, if any
     *
     * @param middleware middleware object array
     * @return blade
     */
    public Blade use(@NonNull WebHook... middleware) {
        if (!BladeKit.isEmpty(middleware)) {
            this.middleware.addAll(Arrays.asList(middleware));
        }
        return this;
    }

    /**
     * Get middleware list
     *
     * @return return middleware list
     */
    public List<WebHook> middleware() {
        return this.middleware;
    }

    /**
     * Set in the name of the app blade application
     *
     * @param appName application name
     * @return blade
     */
    public Blade appName(@NonNull String appName) {
        this.environment(ENV_KEY_APP_NAME, appName);
        return this;
    }

    /**
     * Add a event listener
     * When the trigger event is executed eventListener
     *
     * @param eventType     event type
     * @param eventListener event listener
     * @return blade
     */
    public Blade event(@NonNull EventType eventType, @NonNull EventListener eventListener) {
        eventManager.addEventListener(eventType, eventListener);
        return this;
    }

    /**
     * Get session implements Class Type
     *
     * @return return blade Session Type
     */
    public Class<? extends Session> sessionType() {
        return this.sessionImplType;
    }

    /**
     * Set session implements Class Type, e.g: RedisSession
     *
     * @param sessionImplType Session Type implement
     * @return return blade instance
     */
    public Blade sessionType(Class<? extends Session> sessionImplType) {
        this.sessionImplType = sessionImplType;
        return this;
    }

    /**
     * Event on started
     *
     * @param processor bean processor
     * @return return blade instance
     */
    public Blade onStarted(@NonNull BeanProcessor processor) {
        processors.add(processor);
        return this;
    }

    /**
     * Get processors
     *
     * @return return processors
     */
    public List<BeanProcessor> processors() {
        return processors;
    }

    /**
     * Get EventManager
     *
     * @return return EventManager
     */
    public EventManager eventManager() {
        return eventManager;
    }

    /**
     * Get SessionManager
     *
     * @return return SessionManager
     */
    public SessionManager sessionManager() {
        return sessionManager;
    }

    /**
     * Disable session, default is open
     *
     * @return return blade instance
     */
    public Blade disableSession() {
        this.sessionManager = null;
        return this;
    }

    /**
     * Start blade application.
     * <p>
     * When all the routing in the main function of situations you can use,
     * Otherwise please do not call this method.
     *
     * @return return blade instance
     */
    public Blade start() {
        return this.start(null, null);
    }

    /**
     * Start blade application
     *
     * @param mainCls main Class, the main class bag is basic package
     * @param args    command arguments
     * @return return blade instance
     */
    public Blade start(Class<?> mainCls, String... args) {
        return this.start(mainCls, DEFAULT_SERVER_ADDRESS, DEFAULT_SERVER_PORT, args);
    }

    /**
     * Start the blade web server
     *
     * @param bootClass Start the boot class, used to scan the class in all of the packages
     * @param address   web server bind ip address
     * @param port      web server bind port
     * @param args      launch parameters
     * @return blade
     */
    public Blade start(Class<?> bootClass, @NonNull String address, int port, String... args) {
        try {
            environment.set(ENV_KEY_SERVER_ADDRESS, address);
            Assert.greaterThan(port, 0, "server port not is negative number.");
            this.bootClass = bootClass;
            eventManager.fireEvent(EventType.SERVER_STARTING, this);
            Thread thread = new Thread(() -> {
                try {
                    nettyServer.start(Blade.this, args);
                    latch.countDown();
                    nettyServer.join();
                } catch (Exception e) {
                    startupExceptionHandler.accept(e);
                }
            });
            thread.setName("_(:3」∠)_");
            thread.start();
            started = true;
        } catch (Exception e) {
            startupExceptionHandler.accept(e);
        }
        return this;
    }

    /**
     * Await
     *
     * @return return blade instance
     */
    public Blade await() {
        if (!started) {
            throw new IllegalStateException("Server hasn't been started. Call start() before calling this method.");
        }
        try {
            latch.await();
        } catch (Exception e) {
            log.error("await error", e);
            Thread.currentThread().interrupt();
        }
        return this;
    }

    /**
     * Stop current blade application
     * <p>
     * Will stop synchronization waiting netty service
     */
    public void stop() {
        eventManager.fireEvent(EventType.SERVER_STOPPING, this);
        nettyServer.stopAndWait();
        eventManager.fireEvent(EventType.SERVER_STOPPED, this);
    }

}
