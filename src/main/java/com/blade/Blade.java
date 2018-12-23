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

import com.blade.event.*;
import com.blade.event.EventListener;
import com.blade.exception.BladeException;
import com.blade.ioc.Ioc;
import com.blade.ioc.SimpleIoc;
import com.blade.kit.Assert;
import com.blade.kit.BladeKit;
import com.blade.kit.JsonKit;
import com.blade.kit.StringKit;
import com.blade.kit.reload.FileChangeDetector;
import com.blade.loader.BladeLoader;
import com.blade.mvc.handler.*;
import com.blade.mvc.hook.WebHook;
import com.blade.mvc.http.HttpMethod;
import com.blade.mvc.http.HttpSession;
import com.blade.mvc.http.Session;
import com.blade.mvc.http.session.SessionManager;
import com.blade.mvc.route.RouteMatcher;
import com.blade.mvc.ui.template.DefaultEngine;
import com.blade.mvc.ui.template.TemplateEngine;
import com.blade.security.web.cors.CorsConfiger;
import com.blade.security.web.cors.CorsMiddleware;
import com.blade.server.Server;
import com.blade.server.netty.NettyServer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.var;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.BindException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.blade.mvc.Const.*;

/**
 * Blade Core
 * <p>
 * The Blade is the core operating class of the framework,
 * which can be used to register routes,
 * modify the template engine, set the file list display,
 * static resource directory, and so on.
 *
 * @author biezhi 2017/5/31
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Blade {

    /**
     * Project middleware list,
     * the default is empty, when you use the time you can call the use of methods to add.
     * <p>
     * Blade provide you with BasicAuthMiddleware, CsrfMiddleware,
     * you can customize the implementation of some middleware
     */
    private List<WebHook> middleware = new ArrayList<>();

    /**
     * BeanProcessor list, which stores all the actions that were performed before the project was started
     */
    private List<BeanProcessor> processors = new ArrayList<>();

    /**
     * Blade loader list, which stores all the actions that were performed before the project was started
     */
    private List<BladeLoader> loaders = new ArrayList<>();

    /**
     * All need to be scanned by the package, when you do not set the time will scan com.blade.plugin package
     */
    private Set<String> packages = new LinkedHashSet<>(PLUGIN_PACKAGE_NAME);

    /**
     * All static resource URL prefixes,
     * defaults to "/favicon.ico", "/robots.txt", "/static/", "/upload/", "/webjars/",
     * which are located under classpath
     */
    private Set<String> statics = new HashSet<>(DEFAULT_STATICS);

    /**
     * The default IOC container implementation
     */
    private Ioc ioc = new SimpleIoc();

    /**
     * The default template engine implementation, this is a very simple, generally not put into production
     */
    private TemplateEngine templateEngine = new DefaultEngine();

    /**
     * Event manager, which manages all the guys that will trigger events
     */
    private EventManager eventManager = new EventManager();

    /**
     * Session manager, which manages session when you enable session
     */
    private SessionManager sessionManager = new SessionManager(eventManager);

    /**
     * Used to wait for the start to complete the lock
     */
    private CountDownLatch latch = new CountDownLatch(1);

    /**
     * Web server implementation, currently only netty
     */
    private Server server = new NettyServer();

    /**
     * A route matcher that matches whether a route exists
     */
    private RouteMatcher routeMatcher = new RouteMatcher();

    /**
     * Blade environment, which stores the parameters of the app.properties configuration file
     */
    private Environment environment = Environment.empty();

    /**
     * Exception handling, it will output some logs when the error is initiated
     */
    private Consumer<Exception> startupExceptionHandler = (e) -> log.error("Start blade failed", e);

    /**
     * Exception handler, default is DefaultExceptionHandler.
     * <p>
     * When you need to customize the handling of exceptions can be inherited from DefaultExceptionHandler
     */
    private ExceptionHandler exceptionHandler = new DefaultExceptionHandler();

    private CorsMiddleware corsMiddleware;

    /**
     * Used to identify whether the web server has started
     */
    private boolean started = false;

    /**
     * Project main class, the main category is located in the root directory of the basic package,
     * all the features will be in the sub-package below
     */
    private Class<?> bootClass = null;

    /**
     * Session implementation type, the default is HttpSession.
     * <p>
     * When you need to be able to achieve similar RedisSession
     */
    private Class<? extends Session> sessionImplType = HttpSession.class;

    /**
     * Blade app start banner, default is Const.BANNER
     */
    private String bannerText;

    /**
     * Blade app start thread name, default is Const.DEFAULT_THREAD_NAME
     */
    private String threadName;

    /**
     * Give your blade instance, from then on will get the energy
     *
     * @return return blade instance
     * {@link #of }
     */
    @Deprecated
    public static Blade me() {
        return Blade.of();
    }

    /**
     * Give your blade instance, from then on will get the energy
     *
     * @return return blade instance
     */
    public static Blade of() {
        return new Blade();
    }

    /**
     * Get blade ioc container, default is SimpleIoc implement.
     * <p>
     * IOC container will help you hosting Bean or component, it is actually a Map inside.
     * In the blade in a single way to make objects reuse,
     * you can save resources, to avoid the terrible memory leak
     *
     * @return return ioc container
     */
    public Ioc ioc() {
        return this.ioc;
    }

    /**
     * Add a get route to routes
     *
     * @param path    your route path
     * @param handler route implement
     * @return return blade instance
     * @see #get(String, RouteHandler)
     */
    @Deprecated
    public Blade get(@NonNull String path, @NonNull RouteHandler0 handler) {
        this.routeMatcher.addRoute(path, handler, HttpMethod.GET);
        return this;
    }

    /**
     * Add a get route to routes
     *
     * @param path    your route path
     * @param handler route implement
     * @return return blade instance
     */
    public Blade get(@NonNull String path, @NonNull RouteHandler handler) {
        this.routeMatcher.addRoute(path, handler, HttpMethod.GET);
        return this;
    }

    /**
     * Add a post route to routes
     *
     * @param path    your route path
     * @param handler route implement
     * @return return blade instance
     * @see #post(String, RouteHandler)
     */
    @Deprecated
    public Blade post(@NonNull String path, @NonNull RouteHandler0 handler) {
        this.routeMatcher.addRoute(path, handler, HttpMethod.POST);
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
        this.routeMatcher.addRoute(path, handler, HttpMethod.POST);
        return this;
    }

    /**
     * Add a put route to routes
     *
     * @param path    your route path
     * @param handler route implement
     * @return return blade instance
     * @see #put(String, RouteHandler)
     */
    @Deprecated
    public Blade put(@NonNull String path, @NonNull RouteHandler0 handler) {
        this.routeMatcher.addRoute(path, handler, HttpMethod.PUT);
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
        this.routeMatcher.addRoute(path, handler, HttpMethod.PUT);
        return this;
    }

    /**
     * Add a delete route to routes
     *
     * @param path    your route path
     * @param handler route implement
     * @return return blade instance
     * @see #delete(String, RouteHandler)
     */
    @Deprecated
    public Blade delete(@NonNull String path, @NonNull RouteHandler0 handler) {
        this.routeMatcher.addRoute(path, handler, HttpMethod.DELETE);
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
        this.routeMatcher.addRoute(path, handler, HttpMethod.DELETE);
        return this;
    }

    /**
     * Add a before route to routes, the before route will be executed before matching route
     *
     * @param path    your route path
     * @param handler route implement
     * @return return blade instance
     * @see #before(String, RouteHandler)
     */
    @Deprecated
    public Blade before(@NonNull String path, @NonNull RouteHandler0 handler) {
        this.routeMatcher.addRoute(path, handler, HttpMethod.BEFORE);
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
        this.routeMatcher.addRoute(path, handler, HttpMethod.BEFORE);
        return this;
    }

    /**
     * Add a after route to routes, the before route will be executed after matching route
     *
     * @param path    your route path
     * @param handler route implement
     * @return return blade instance
     * @see #after(String, RouteHandler)
     */
    @Deprecated
    public Blade after(@NonNull String path, @NonNull RouteHandler0 handler) {
        this.routeMatcher.addRoute(path, handler, HttpMethod.AFTER);
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
        this.routeMatcher.addRoute(path, handler, HttpMethod.AFTER);
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
        return this.templateEngine;
    }

    /**
     * Get RouteMatcher
     *
     * @return return RouteMatcher
     */
    public RouteMatcher routeMatcher() {
        return this.routeMatcher;
    }

    /**
     * Register bean to ioc container
     *
     * @param bean bean object
     * @return blade
     */
    public Blade register(@NonNull Object bean) {
        this.ioc.addBean(bean);
        return this;
    }

    /**
     * Register bean to ioc container
     *
     * @param cls bean class, the class must provide a no args constructor
     * @return blade
     */
    public Blade register(@NonNull Class<?> cls) {
        this.ioc.addBean(cls);
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
        this.statics.addAll(Arrays.asList(folders));
        return this;
    }

    /**
     * Set whether to show the file directory, default doesn't show
     *
     * @param fileList show the file directory
     * @return blade
     */
    public Blade showFileList(boolean fileList) {
        this.environment.set(ENV_KEY_STATIC_LIST, fileList);
        return this;
    }

    /**
     * Set whether open gzip, default disabled
     *
     * @param gzipEnable enabled gzip
     * @return blade
     */
    public Blade gzip(boolean gzipEnable) {
        this.environment.set(ENV_KEY_GZIP_ENABLE, gzipEnable);
        return this;
    }

    /**
     * Get ioc bean
     *
     * @param cls bean class type
     * @return return bean instance
     */
    public <T> T getBean(@NonNull Class<T> cls) {
        return this.ioc.getBean(cls);
    }

    /**
     * Get ExceptionHandler
     *
     * @return return ExceptionHandler
     */
    public ExceptionHandler exceptionHandler() {
        return this.exceptionHandler;
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
        return this.environment.getBoolean(ENV_KEY_DEV_MODE, true);
    }

    /**
     * Whether encoding setting mode for developers
     * The default mode is developers
     *
     * @param devMode developer mode
     * @return blade
     */
    public Blade devMode(boolean devMode) {
        this.environment.set(ENV_KEY_DEV_MODE, devMode);
        return this;
    }

    public boolean isAutoRefreshDir() {
        return this.environment.get(ENV_KEY_AUTO_REFRESH_DIR).isPresent();
    }

    public void setAutoRefreshDir(String dir) {
        this.environment.set(ENV_KEY_AUTO_REFRESH_DIR, dir);
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
        this.enableCors(enableCors, new CorsConfiger());
        return this;
    }

    /**
     * Set whether to config  cors
     * @param enableCors enable cors
     * @param corsConfig config cors
     * @return blade
     */
    public Blade enableCors(boolean enableCors, CorsConfiger corsConfig) {
        this.environment.set(ENV_KEY_CORS_ENABLE, enableCors);
        if (enableCors) {
            this.corsMiddleware = new CorsMiddleware(corsConfig);
        }
        return this;
    }

    public CorsMiddleware corsMiddleware() {
        return corsMiddleware;
    }

    /**
     * Get blade statics list.
     * e.g: "/favicon.ico", "/robots.txt", "/static/", "/upload/", "/webjars/"
     *
     * @return return statics
     */
    public Set<String> getStatics() {
        return this.statics;
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
     * Without setting will read the classpath -> application.properties
     *
     * @param bootConf boot config file name
     * @return blade
     */
    public Blade bootConf(@NonNull String bootConf) {
        this.environment.set(ENV_KEY_BOOT_CONF, bootConf);
        return this;
    }

    /**
     * Set the environment variable for global use here
     * <p>
     * {@link #env(String, String)}
     *
     * @param key   environment key
     * @param value environment value
     * @return blade
     */
    @Deprecated
    public Blade environment(@NonNull String key, @NonNull Object value) {
        this.environment.set(key, value);
        return this;
    }

    /**
     * Return the application's environment configuration information.
     *
     * @return Environment
     */
    public Environment environment() {
        return this.environment;
    }

    @Deprecated
    public Blade environment(Environment environment) {
        this.environment = environment;
        return this;
    }

    /**
     * Get application environment information.
     *
     * @param key environment key
     * @return environment optional value
     */
    public Optional<String> env(String key) {
        return this.environment.get(key);
    }

    /**
     * Get application environment information.
     *
     * @param key          environment key
     * @param defaultValue default value, if value is null
     * @return environment optional value
     */
    public String env(String key, String defaultValue) {
        return this.environment.get(key, defaultValue);
    }

    /**
     * Set to start the web server to monitor port, the default is 9000
     *
     * @param port web server port, default is 9000
     * @return blade
     */
    public Blade listen(int port) {
        Assert.greaterThan(port, 0, "server port not is negative number.");
        this.environment.set(ENV_KEY_SERVER_PORT, port);
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
        this.environment.set(ENV_KEY_SERVER_ADDRESS, address);
        this.environment.set(ENV_KEY_SERVER_PORT, port);
        return this;
    }

    /**
     * The use of multiple middleware, if any
     *
     * @param middleware middleware object array
     * @return blade
     */
    public Blade use(@NonNull WebHook... middleware) {
        if (BladeKit.isEmpty(middleware)) {
            return this;
        }
        this.middleware.addAll(Arrays.asList(middleware));
        for (var webHook : middleware) {
            this.register(webHook);
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
        this.environment.set(ENV_KEY_APP_NAME, appName);
        return this;
    }

    /**
     * Add a event watcher
     * When the trigger event is executed eventListener
     *
     * @param eventType     event type
     * @param eventListener event watcher
     * @return blade
     */
    public Blade event(@NonNull EventType eventType, @NonNull EventListener eventListener) {
        this.eventManager.addEventListener(eventType, eventListener);
        return this;
    }

    /**
     * Add a event watcher
     * When the trigger event is executed eventListener
     *
     * @param eventType     event type
     * @param eventListener event watcher
     * @return blade
     */
    public Blade on(@NonNull EventType eventType, @NonNull EventListener eventListener) {
        this.eventManager.addEventListener(eventType, eventListener);
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
    @Deprecated
    public Blade onStarted(@NonNull BeanProcessor processor) {
        this.processors.add(processor);
        return this;
    }

    /**
     * Add blade loader
     *
     * @param loader
     * @return
     */
    public Blade addLoader(@NonNull BladeLoader loader) {
        this.loaders.add(loader);
        return this;
    }

    /**
     * Get processors
     *
     * @return return processors
     */
    @Deprecated
    public List<BeanProcessor> processors() {
        return this.processors;
    }

    public List<BladeLoader> loaders() {
        return this.loaders;
    }

    /**
     * Get EventManager
     *
     * @return return EventManager
     */
    public EventManager eventManager() {
        return this.eventManager;
    }

    /**
     * Get SessionManager
     *
     * @return return SessionManager
     */
    public SessionManager sessionManager() {
        return this.sessionManager;
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

    public Blade watchEnvChange(boolean watchEnvChange) {
        this.environment.set(ENV_KEY_APP_WATCH_ENV, watchEnvChange);
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
        try {
            this.loadConfig(args);

            this.bootClass = mainCls;
            eventManager.fireEvent(EventType.SERVER_STARTING, new Event().attribute("blade", this));

            Thread thread = new Thread(() -> {
                try {
                    server.start(Blade.this);
                    latch.countDown();
                    server.join();
                } catch (BindException e) {
                    log.error("Bind address error\n", e);
                    System.exit(0);
                } catch (Exception e) {
                    startupExceptionHandler.accept(e);
                }
            });

            String threadName = null != this.threadName ? this.threadName : environment.get(ENV_KEY_APP_THREAD_NAME, null);
            threadName = null != threadName ? threadName : DEFAULT_THREAD_NAME;

            thread.setName(threadName);
            thread.start();

            this.started = true;

            Thread resourceFilesRefreshThread = new Thread(() -> {
                try {
                    FileChangeDetector fileChangeDetector = new FileChangeDetector(environment.get(ENV_KEY_AUTO_REFRESH_DIR).get());
                    fileChangeDetector.processEvent((event, filePath) -> {
                        try {
                            //TODO: add support for Create and Delete
                            if (event.equals(StandardWatchEventKinds.ENTRY_MODIFY)) {
                                Path destPath = FileChangeDetector.getDestPath(filePath, environment);
                                Files.copy(filePath, destPath, StandardCopyOption.REPLACE_EXISTING);
                            }
                        } catch (IOException e) {
                            log.error("Exception when trying to copy updated file");
                            startupExceptionHandler.accept(e);
                        }
                    });
                } catch (IOException e) {
                    startupExceptionHandler.accept(e);
                }
            });

            if (devMode() && isAutoRefreshDir()) {
                log.info("auto refresh is enabled");
                resourceFilesRefreshThread.start();
            }
        } catch (Exception e) {
            startupExceptionHandler.accept(e);
        }
        return this;

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
    @Deprecated
    public Blade start(Class<?> bootClass, @NonNull String address, int port, String... args) {
        return this;
    }

    /**
     * Await web server started
     *
     * @return return blade instance
     */
    public Blade await() {
        if (!this.started) {
            throw new IllegalStateException("Server hasn't been started. Call start() before calling this method.");
        }
        try {
            this.latch.await();
        } catch (Exception e) {
            log.error("Blade start await error", e);
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
        this.eventManager.fireEvent(EventType.SERVER_STOPPING, new Event().attribute("blade", this));
        this.server.stopAndWait();
        this.eventManager.fireEvent(EventType.SERVER_STOPPED, new Event().attribute("blade", this));
    }

    /**
     * Register WebSocket path
     *
     * @param path    websocket path
     * @param handler websocket handler
     * @return return blade instance
     */
    public Blade webSocket(@NonNull String path, @NonNull WebSocketHandler handler) {
        this.routeMatcher.addWebSocket(path,handler);
        return this;
    }

    /**
     * Set blade start banner text
     *
     * @param bannerText banner text
     * @return return blade instance
     */
    public Blade bannerText(String bannerText) {
        this.bannerText = bannerText;
        return this;
    }

    /**
     * Get banner text
     *
     * @return return blade start banner text
     */
    public String bannerText() {
        if (null != bannerText) return bannerText;
        String bannerPath = environment.get(ENV_KEY_BANNER_PATH, null);

        if (StringKit.isEmpty(bannerPath) || Files.notExists(Paths.get(bannerPath))) {
            return null;
        }

        try {
            BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(bannerPath));
            bannerText = bufferedReader.lines().collect(Collectors.joining("\r\n"));
        } catch (Exception e) {
            log.error("Load Start Banner file error", e);
        }
        return bannerText;
    }

    /**
     * Set blade start thread name
     *
     * @param threadName thread name
     * @return return blade instance
     */
    public Blade threadName(String threadName) {
        this.threadName = threadName;
        return this;
    }

    /**
     * Set context path, default is "/"
     *
     * @param contextPath context path
     * @return return blade instance
     * @since 2.0.8-RELEASE
     */
    public Blade contextPath(String contextPath) {
        this.environment.set(ENV_KEY_CONTEXT_PATH, contextPath);
        return this;
    }

    /**
     * Load application environment configuration
     *
     * @param args command line parameters
     */
    private void loadConfig(String[] args) {
        String      bootConf = environment().get(ENV_KEY_BOOT_CONF, PROP_NAME);
        Environment bootEnv  = Environment.of(bootConf);
        String      envName  = "default";

        if (null == bootEnv || bootEnv.isEmpty()) {
            bootEnv = Environment.of(PROP_NAME0);
        }

        if (!Objects.requireNonNull(bootEnv).isEmpty()) {
            Map<String, String>            bootEnvMap = bootEnv.toMap();
            Set<Map.Entry<String, String>> entrySet   = bootEnvMap.entrySet();
            entrySet.forEach(entry -> environment.set(entry.getKey(), entry.getValue()));
        }

        Map<String, String> argsMap = BladeKit.parseArgs(args);
        if (null != argsMap && !argsMap.isEmpty()) {
            log.info(" command line args: {}", JsonKit.toString(argsMap));
        }

        if (StringKit.isNotEmpty(argsMap.get(ENV_KEY_APP_ENV))) {
            envName = argsMap.get(ENV_KEY_APP_ENV);
            String      evnFileName = "application-" + envName + ".properties";
            Environment customEnv   = Environment.of(evnFileName);
            if (customEnv != null && !customEnv.isEmpty()) {
                customEnv.props().forEach((key, value) -> this.environment.set(key.toString(), value));
            } else {
                // compatible with older versions
                evnFileName = "app-" + envName + ".properties";
                customEnv = Environment.of(evnFileName);

                if (customEnv != null && !customEnv.isEmpty()) {
                    Iterator<Map.Entry<Object, Object>> iterator = customEnv.props().entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<Object, Object> next = iterator.next();
                        this.environment.set(next.getKey().toString(), next.getValue());
                    }
                }
            }
            argsMap.remove(ENV_KEY_APP_ENV);
        }

        this.environment.set(ENV_KEY_APP_ENV, envName);

        this.register(this.environment);

        // load terminal param
        if (BladeKit.isEmpty(args)) {
            return;
        }

        Iterator<Map.Entry<String, String>> iterator = argsMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> next = iterator.next();
            this.environment.set(next.getKey(), next.getValue());
        }

    }

}