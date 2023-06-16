/*
  Copyright (c) 2022, katon (hellokaton@gmail.com)
  <p>
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
  <p>
  http://www.apache.org/licenses/LICENSE-2.0
  <p>
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */
package com.hellokaton.blade;

import com.hellokaton.blade.event.Event;
import com.hellokaton.blade.event.EventManager;
import com.hellokaton.blade.event.EventType;
import com.hellokaton.blade.ioc.Ioc;
import com.hellokaton.blade.ioc.SimpleIoc;
import com.hellokaton.blade.kit.*;
import com.hellokaton.blade.kit.reload.FileChangeDetector;
import com.hellokaton.blade.loader.BladeLoader;
import com.hellokaton.blade.mvc.BladeConst;
import com.hellokaton.blade.mvc.handler.DefaultExceptionHandler;
import com.hellokaton.blade.mvc.handler.ExceptionHandler;
import com.hellokaton.blade.mvc.handler.RouteHandler;
import com.hellokaton.blade.mvc.hook.WebHook;
import com.hellokaton.blade.mvc.http.HttpMethod;
import com.hellokaton.blade.mvc.http.session.SessionManager;
import com.hellokaton.blade.mvc.route.RouteMatcher;
import com.hellokaton.blade.mvc.ui.template.DefaultEngine;
import com.hellokaton.blade.mvc.ui.template.TemplateEngine;
import com.hellokaton.blade.options.CorsOptions;
import com.hellokaton.blade.options.HttpOptions;
import com.hellokaton.blade.options.StaticOptions;
import com.hellokaton.blade.server.INettySslCustomizer;
import com.hellokaton.blade.server.NettyServer;
import com.hellokaton.blade.server.Server;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.BindException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.hellokaton.blade.mvc.BladeConst.ENV_KEY_FAVICON_DIR;

/**
 * Blade Core
 * <p>
 * The Blade is the core operating class of the framework,
 * which can be used to register routes,
 * modify the template engine, set the file list display,
 * static resource directory, and so on.
 *
 * @author hellokaton 2017/5/31
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Blade {

    /**
     * Blade loader list, which stores all the actions that were performed before the project was started
     */
    private final List<BladeLoader> loaders = new ArrayList<>();

    /**
     * All need to be scanned by the package, when you do not set the time will scan com.hellokaton.blade.plugin package
     */
    private final Set<String> packages = new LinkedHashSet<>(BladeConst.PLUGIN_PACKAGE_NAME);

    /**
     * The default IOC container implementation
     */
    private final Ioc ioc = new SimpleIoc();

    /**
     * The default template engine implementation, this is a very simple, generally not put into production
     */
    private TemplateEngine templateEngine = new DefaultEngine();

    /**
     * Event manager, which manages all the guys that will trigger events
     */
    private final EventManager eventManager = new EventManager();

    /**
     * Session manager, which manages session when you enable session
     */
    private SessionManager sessionManager = new SessionManager(eventManager);

    /**
     * Used to wait for the start to complete the lock
     */
    private final CountDownLatch latch = new CountDownLatch(1);

    /**
     * Web server implementation, currently only netty
     */
    private final Server server = new NettyServer();

    /**
     * A route matcher that matches whether a route exists
     */
    private final RouteMatcher routeMatcher = new RouteMatcher();

    private CorsOptions corsOptions = null;
    private HttpOptions httpOptions = HttpOptions.create();
    private StaticOptions staticOptions = StaticOptions.create();

    /**
     * An SSL customizer for Netty.  If set it will supercede
     * the built-in SSL options.
     */
    private INettySslCustomizer nettySslCustomizer = null;


    /**
     * Blade environment, which stores the parameters of the application.properties configuration file
     */
    private Environment environment = Environment.empty();

    /**
     * Exception handling, it will output some logs when the error is initiated
     */
    private final Consumer<Exception> startupExceptionHandler = (e) -> log.error("Start blade failed", e);

    /**
     * Exception handler, default is DefaultExceptionHandler.
     * <p>
     * When you need to customize the handling of exceptions can be inherited from DefaultExceptionHandler
     */
    private ExceptionHandler exceptionHandler = new DefaultExceptionHandler();

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
     */
    public static Blade create() {
        return new Blade();
    }

    @Deprecated
    public static Blade of() {
        return create();
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
     * @return blade instance
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
     */
    public Blade after(@NonNull String path, @NonNull RouteHandler handler) {
        this.routeMatcher.addRoute(path, handler, HttpMethod.AFTER);
        return this;
    }

    /**
     * Setting blade mvc default templateEngine
     *
     * @param templateEngine TemplateEngine object
     * @return blade instance
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
     * setting favicon dir, default is /static
     *
     * @param faviconDir favicon dir
     * @return blade instance
     */
    public Blade faviconDir(String faviconDir) {
        this.setEnv(ENV_KEY_FAVICON_DIR, faviconDir);
        return this;
    }

    /**
     * Get RouteMatcher
     *
     * @return return RouteMatcher
     */
    public RouteMatcher routeMatcher() {
        return this.routeMatcher;
    }

    public Blade http(Consumer<HttpOptions> consumer) {
        consumer.accept(this.httpOptions);
        return this;
    }

    public Blade http(HttpOptions httpOptions) {
        this.httpOptions = httpOptions;
        return this;
    }

    public Blade staticOptions(Consumer<StaticOptions> consumer) {
        consumer.accept(this.staticOptions);
        return this;
    }

    public Blade staticOptions(StaticOptions staticOptions) {
        this.staticOptions = staticOptions;
        return this;
    }

    public Blade cors(CorsOptions corsOptions) {
        this.corsOptions = corsOptions;
        return this;
    }

    public CorsOptions corsOptions() {
        return this.corsOptions;
    }

    public HttpOptions httpOptions() {
        return this.httpOptions;
    }

    public StaticOptions staticOptions() {
        return this.staticOptions;
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
        return this.environment.getBoolean(BladeConst.ENV_KEY_DEV_MODE, true);
    }

    /**
     * Whether encoding setting mode for developers
     * The default mode is developers
     *
     * @param devMode developer mode
     * @return blade
     */
    public Blade devMode(boolean devMode) {
        this.environment.set(BladeConst.ENV_KEY_DEV_MODE, devMode);
        return this;
    }

    public boolean isAutoRefreshDir() {
        return this.environment.get(BladeConst.ENV_KEY_AUTO_REFRESH_DIR).isPresent();
    }

    public void setAutoRefreshDir(String dir) {
        this.environment.set(BladeConst.ENV_KEY_AUTO_REFRESH_DIR, dir);
    }

    public Class<?> bootClass() {
        return this.bootClass;
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
        this.environment.set(BladeConst.ENV_KEY_BOOT_CONF, bootConf);
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
    public Optional<String> getEnv(String key) {
        return this.environment.get(key);
    }

    /**
     * Get application environment information.
     *
     * @param key          environment key
     * @param defaultValue default value, if value is null
     * @return environment optional value
     */
    public String getEnv(String key, String defaultValue) {
        return this.environment.get(key, defaultValue);
    }

    public Blade setEnv(String key, Object value) {
        this.environment.set(key, value);
        return this;
    }

    /**
     * Set to start the web server to monitor port, the default is 9000
     *
     * @return blade
     */
    public Blade listen() {
        return listen(BladeConst.DEFAULT_SERVER_PORT);
    }

    /**
     * Set to start the web server to monitor port, the default is 9000
     *
     * @param port web server port, default is 9000
     * @return blade
     */
    public Blade listen(int port) {
        Assert.greaterThan(port, 0, "server port is not negative number.");
        this.environment.set(BladeConst.ENV_KEY_SERVER_PORT, port);
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
        Assert.greaterThan(port, 0, "server port is not negative number.");
        this.environment.set(BladeConst.ENV_KEY_SERVER_ADDRESS, address);
        this.environment.set(BladeConst.ENV_KEY_SERVER_PORT, port);
        return this;
    }

    /**
     * Use of multiple middleware
     *
     * @param middleware middleware array
     * @return blade
     */
    public Blade use(@NonNull WebHook... middleware) {
        if (BladeKit.isEmpty(middleware)) {
            return this;
        }
        for (WebHook webHook : middleware) {
            this.routeMatcher.addMiddleware(webHook);
            this.register(webHook);
        }
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
    public Blade event(@NonNull EventType eventType, @NonNull com.hellokaton.blade.event.EventListener eventListener) {
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
    public Blade on(@NonNull EventType eventType, @NonNull com.hellokaton.blade.event.EventListener eventListener) {
        this.eventManager.addEventListener(eventType, eventListener);
        return this;
    }

    /**
     * Add blade loader
     *
     * @param loader see {@link BladeLoader}
     * @return Blade
     */
    public Blade addLoader(@NonNull BladeLoader loader) {
        this.loaders.add(loader);
        return this;
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
        this.environment.set(BladeConst.ENV_KEY_APP_WATCH_ENV, watchEnvChange);
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
    public Blade start(String... args) {
        Class<?> caller = Arrays.stream(Thread.currentThread().getStackTrace())
                .filter(st -> "main".equals(st.getMethodName()))
                .findFirst()
                .map(StackTraceElement::getClassName)
                .map(UncheckedFnKit.function(Class::forName))
                .orElse(null);
        return this.start(caller, args);
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

            String threadName = null != this.threadName ? this.threadName : environment.get(BladeConst.ENV_KEY_APP_THREAD_NAME, null);
            threadName = null != threadName ? threadName : BladeConst.DEFAULT_THREAD_NAME;

            thread.setName(threadName);
            thread.start();

            this.started = true;

            Thread resourceFilesRefreshThread = new Thread(() -> {
                try {
                    FileChangeDetector fileChangeDetector = new FileChangeDetector(environment.get(BladeConst.ENV_KEY_AUTO_REFRESH_DIR).get());
                    fileChangeDetector.processEvent((event, filePath) -> {
                        try {
                            //TODO: add support for Create and Delete
                            if (event.equals(StandardWatchEventKinds.ENTRY_MODIFY)) {
                                Path destPath = FileChangeDetector.getDestPath(filePath, environment, staticOptions);
                                Files.copy(filePath, Objects.requireNonNull(destPath), StandardCopyOption.REPLACE_EXISTING);
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
     * <Code>INettySslCustomizer</code> will permit the developer
     * to customize the Netty SSLContext as desired.
     * @return
     */
    public INettySslCustomizer getNettySslCustomizer() {
		return this.nettySslCustomizer;
	}

	public Blade setNettySslCustomizer(INettySslCustomizer customizer) {
		this.nettySslCustomizer = customizer;
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
        String bannerPath = environment.get(BladeConst.ENV_KEY_BANNER_PATH, null);

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
        this.environment.set(BladeConst.ENV_KEY_CONTEXT_PATH, contextPath);
        return this;
    }

    /**
     * Load application environment configuration
     *
     * @param args command line parameters
     */
    private void loadConfig(String[] args) {
        String bootConf = environment().get(BladeConst.ENV_KEY_BOOT_CONF, BladeConst.PROP_NAME);
        Environment bootEnv = Environment.of(bootConf);
        String envName = "default";

        if (!Objects.requireNonNull(bootEnv).isEmpty()) {
            Map<String, String> bootEnvMap = bootEnv.toMap();
            Set<Map.Entry<String, String>> entrySet = bootEnvMap.entrySet();
            entrySet.forEach(entry -> environment.set(entry.getKey(), entry.getValue()));
        }

        Map<String, String> argsMap = BladeKit.parseArgs(args);
        if (!argsMap.isEmpty()) {
            log.info(" command line args: {}", JsonKit.toString(argsMap));
        }

        if (StringKit.isNotEmpty(argsMap.get(BladeConst.ENV_KEY_APP_ENV))) {
            envName = argsMap.get(BladeConst.ENV_KEY_APP_ENV);
            String evnFileName = "application-" + envName + ".properties";
            Environment customEnv = Environment.of(evnFileName);
            if (customEnv != null && !customEnv.isEmpty()) {
                customEnv.props().forEach((key, value) -> this.environment.set(key.toString(), value));
            } else {
                // compatible with older versions
                evnFileName = "app-" + envName + ".properties";
                customEnv = Environment.of(evnFileName);

                if (customEnv != null && !customEnv.isEmpty()) {
                    for (Map.Entry<Object, Object> next : customEnv.props().entrySet()) {
                        this.environment.set(next.getKey().toString(), next.getValue());
                    }
                }
            }
            argsMap.remove(BladeConst.ENV_KEY_APP_ENV);
        }

        this.environment.set(BladeConst.ENV_KEY_APP_ENV, envName);

        this.register(this.environment);

        // load terminal param
        if (BladeKit.isEmpty(args)) {
            return;
        }

        for (Map.Entry<String, String> next : argsMap.entrySet()) {
            this.environment.set(next.getKey(), next.getValue());
        }

    }

}
