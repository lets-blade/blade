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
package com.blade.server.netty;

import com.blade.Blade;
import com.blade.Environment;
import com.blade.event.BeanProcessor;
import com.blade.event.Event;
import com.blade.event.EventType;
import com.blade.ioc.DynamicContext;
import com.blade.ioc.Ioc;
import com.blade.ioc.annotation.Bean;
import com.blade.ioc.annotation.Configuration;
import com.blade.ioc.annotation.Value;
import com.blade.ioc.bean.BeanDefine;
import com.blade.ioc.bean.ClassInfo;
import com.blade.ioc.bean.OrderComparator;
import com.blade.kit.*;
import com.blade.loader.BladeLoader;
import com.blade.mvc.Const;
import com.blade.mvc.WebContext;
import com.blade.mvc.annotation.Path;
import com.blade.mvc.annotation.URLPattern;
import com.blade.mvc.annotation.WebSocket;
import com.blade.mvc.handler.DefaultExceptionHandler;
import com.blade.mvc.handler.ExceptionHandler;
import com.blade.mvc.handler.WebSocketHandler;
import com.blade.mvc.handler.WebSocketHandlerWrapper;
import com.blade.mvc.hook.WebHook;
import com.blade.mvc.http.session.SessionCleaner;
import com.blade.mvc.route.RouteBuilder;
import com.blade.mvc.route.RouteMatcher;
import com.blade.mvc.ui.template.DefaultEngine;
import com.blade.server.Server;
import com.blade.task.Task;
import com.blade.task.TaskContext;
import com.blade.task.TaskManager;
import com.blade.task.TaskStruct;
import com.blade.task.annotation.Schedule;
import com.blade.task.cron.CronExecutorService;
import com.blade.task.cron.CronExpression;
import com.blade.task.cron.CronThreadPoolExecutor;
import com.blade.watcher.EnvironmentWatcher;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.DefaultEventLoop;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.util.ResourceLeakDetector;
import lombok.extern.slf4j.Slf4j;
import lombok.var;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static com.blade.kit.BladeKit.getPrefixSymbol;
import static com.blade.kit.BladeKit.getStartedSymbol;
import static com.blade.mvc.Const.*;

/**
 * Netty Web Server
 *
 * @author biezhi
 * 2017/5/31
 */
@Slf4j
public class NettyServer implements Server {

    private Blade               blade;
    private Environment         environment;
    private EventLoopGroup      bossGroup;
    private EventLoop           scheduleEventLoop;
    private EventLoopGroup      workerGroup;
    private Channel             channel;
    private RouteBuilder        routeBuilder;
    private List<BeanProcessor> processors;
    private List<BladeLoader>   loaders;
    private List<TaskStruct>    taskStruts = new ArrayList<>();

    private volatile boolean isStop;

    @Override
    public void start(Blade blade) throws Exception {
        this.blade = blade;
        this.environment = blade.environment();
        this.processors = blade.processors();
        this.loaders = blade.loaders();

        long startMs = System.currentTimeMillis();

        int padSize = 16;
        log.info("{} {}{}", StringKit.padRight("app.env", padSize), getPrefixSymbol(), environment.get(ENV_KEY_APP_ENV, "default"));
        log.info("{} {}{}", StringKit.padRight("app.pid", padSize), getPrefixSymbol(), BladeKit.getPID());
        log.info("{} {}{}", StringKit.padRight("app.devMode", padSize), getPrefixSymbol(), blade.devMode());

        log.info("{} {}{}", StringKit.padRight("jdk.version", padSize), getPrefixSymbol(), System.getProperty("java.version"));
        log.info("{} {}{}", StringKit.padRight("user.dir", padSize), getPrefixSymbol(), System.getProperty("user.dir"));
        log.info("{} {}{}", StringKit.padRight("java.io.tmpdir", padSize), getPrefixSymbol(), System.getProperty("java.io.tmpdir"));
        log.info("{} {}{}", StringKit.padRight("user.timezone", padSize), getPrefixSymbol(), System.getProperty("user.timezone"));
        log.info("{} {}{}", StringKit.padRight("file.encoding", padSize), getPrefixSymbol(), System.getProperty("file.encoding"));
        log.info("{} {}{}", StringKit.padRight("app.classpath", padSize), getPrefixSymbol(), CLASSPATH);

        this.initConfig();

        String contextPath = environment.get(ENV_KEY_CONTEXT_PATH, "/");
        WebContext.init(blade, contextPath);

        this.initIoc();
        this.watchEnv();
        this.startServer(startMs);
        this.sessionCleaner();
        this.startTask();
        this.shutdownHook();
    }

    private void sessionCleaner() {
        if (null != blade.sessionManager()) {
            scheduleEventLoop.
                    scheduleWithFixedDelay(new SessionCleaner(blade.sessionManager()),
                            1000, 1000, TimeUnit.MILLISECONDS);
        }
    }

    private void initIoc() {
        RouteMatcher routeMatcher = blade.routeMatcher();
        routeMatcher.initMiddleware(blade.middleware());

        routeBuilder = new RouteBuilder(routeMatcher);

        blade.scanPackages().stream()
                .flatMap(DynamicContext::recursionFindClasses)
                .map(ClassInfo::getClazz)
                .filter(ReflectKit::isNormalClass)
                .forEach(this::parseAndCreate);

        routeMatcher.register();

        this.loaders.stream().sorted(new OrderComparator<>()).forEach(b -> b.preLoad(blade));
        this.processors.stream().sorted(new OrderComparator<>()).forEach(b -> b.preHandle(blade));

        Ioc ioc = blade.ioc();
        if (BladeKit.isNotEmpty(ioc.getBeans())) {
            log.info("{}Register bean: {}", getStartedSymbol(), ioc.getBeans());
        }

        List<BeanDefine> beanDefines = ioc.getBeanDefines();

        if (BladeKit.isNotEmpty(beanDefines)) {
            beanDefines.forEach(b -> {
                IocKit.initInjection(ioc, b);
                IocKit.injectionValue(environment, b);
                List<TaskStruct> cronExpressions = BladeKit.getTasks(b.getType(), environment);
                if (null != cronExpressions) {
                    taskStruts.addAll(cronExpressions);
                }
            });
        }
        this.loaders.stream().sorted(new OrderComparator<>()).forEach(b -> b.load(blade));
        this.processors.stream().sorted(new OrderComparator<>()).forEach(b -> b.processor(blade));
    }

    private void startServer(long startMs) throws Exception {

        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.DISABLED);

        boolean SSL = environment.getBoolean(ENV_KEY_SSL, false);
        // Configure SSL.
        SslContext sslCtx = null;
        if (SSL) {
            String certFilePath       = environment.get(ENV_KEY_SSL_CERT, null);
            String privateKeyPath     = environment.get(ENE_KEY_SSL_PRIVATE_KEY, null);
            String privateKeyPassword = environment.get(ENE_KEY_SSL_PRIVATE_KEY_PASS, null);

            log.info("{}SSL CertChainFile  Path: {}", getStartedSymbol(), certFilePath);
            log.info("{}SSL PrivateKeyFile Path: {}", getStartedSymbol(), privateKeyPath);
            sslCtx = SslContextBuilder.forServer(new File(certFilePath), new File(privateKeyPath), privateKeyPassword).build();
        }

        var bootstrap = new ServerBootstrap();

        int acceptThreadCount = environment.getInt(ENC_KEY_NETTY_ACCEPT_THREAD_COUNT, DEFAULT_ACCEPT_THREAD_COUNT);
        int ioThreadCount     = environment.getInt(ENV_KEY_NETTY_IO_THREAD_COUNT, DEFAULT_IO_THREAD_COUNT);

        // enable epoll
        if (BladeKit.epollIsAvailable()) {
            log.info("{}Use EpollEventLoopGroup", getStartedSymbol());
            bootstrap.option(EpollChannelOption.SO_REUSEPORT, true);

            NettyServerGroup nettyServerGroup = EpollKit.group(acceptThreadCount, ioThreadCount);
            this.bossGroup = nettyServerGroup.getBoosGroup();
            this.workerGroup = nettyServerGroup.getWorkerGroup();
            bootstrap.group(bossGroup, workerGroup).channel(nettyServerGroup.getSocketChannel());
        } else {
            log.info("{}Use NioEventLoopGroup", getStartedSymbol());

            this.bossGroup = new NioEventLoopGroup(acceptThreadCount, new NamedThreadFactory("boss@"));
            this.workerGroup = new NioEventLoopGroup(ioThreadCount, new NamedThreadFactory("worker@"));
            bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class);
        }

        scheduleEventLoop = new DefaultEventLoop();

        bootstrap.childHandler(new HttpServerInitializer(sslCtx, blade, scheduleEventLoop));

        String  address = environment.get(ENV_KEY_SERVER_ADDRESS, DEFAULT_SERVER_ADDRESS);
        Integer port    = environment.getInt(ENV_KEY_SERVER_PORT, DEFAULT_SERVER_PORT);

        channel = bootstrap.bind(address, port).sync().channel();

        String appName  = environment.get(ENV_KEY_APP_NAME, "Blade");
        String url      = Ansi.BgRed.and(Ansi.Black).format(" %s:%d ", address, port);
        String protocol = SSL ? "https" : "http";

        log.info("{}{} initialize successfully, Time elapsed: {} ms", getStartedSymbol(), appName, (System.currentTimeMillis() - startMs));
        log.info("{}Blade start with {}", getStartedSymbol(), url);
        log.info("{}Open browser access {}://{}:{} ⚡\r\n", getStartedSymbol(), protocol, address.replace(DEFAULT_SERVER_ADDRESS, LOCAL_IP_ADDRESS), port);

        blade.eventManager().fireEvent(EventType.SERVER_STARTED, new Event().attribute("blade", blade));
    }

    private void startTask() {
        if (taskStruts.isEmpty()) {
            return;
        }

        int corePoolSize = environment.getInt(ENV_KEY_TASK_THREAD_COUNT, Runtime.getRuntime().availableProcessors() + 1);

        CronExecutorService executorService = TaskManager.getExecutorService();
        if (null == executorService) {
            executorService = new CronThreadPoolExecutor(corePoolSize, new NamedThreadFactory("task@"));
            TaskManager.init(executorService);
        }

        var jobCount = new AtomicInteger();
        for (var taskStrut : taskStruts) {
            addTask(executorService, jobCount, taskStrut);
        }
    }

    private void addTask(CronExecutorService executorService, AtomicInteger jobCount, TaskStruct taskStruct) {
        try {
            Schedule    schedule    = taskStruct.getSchedule();
            String      cron        = taskStruct.getCron();
            String      jobName     = StringKit.isBlank(schedule.name()) ? "task-" + jobCount.getAndIncrement() : schedule.name();
            Task        task        = new Task(jobName, new CronExpression(cron), schedule.delay());
            TaskContext taskContext = new TaskContext(task);

            task.setTask(() -> {
                Object target = blade.ioc().getBean(taskStruct.getType());
                Method method = taskStruct.getMethod();
                try {
                    if (method.getParameterCount() == 1 && method.getParameterTypes()[0].equals(TaskContext.class)) {
                        taskStruct.getMethod().invoke(target, taskContext);
                    } else {
                        taskStruct.getMethod().invoke(target);
                    }
                } catch (Exception e) {
                    log.error("Task method error", e);
                }
            });

            ScheduledFuture future = executorService.submit(task);
            task.setFuture(future);
            TaskManager.addTask(task);
        } catch (Exception e) {
            log.warn("{}Add task fail: {}", getPrefixSymbol(), e.getMessage());
        }
    }

    private void parseAndCreate(Class<?> clazz) {
        if (null != clazz.getAnnotation(Bean.class) || null != clazz.getAnnotation(Value.class)) {
            blade.register(clazz);
        }
        if (null != clazz.getAnnotation(Path.class)) {
            if (null == blade.getBean(clazz)) {
                blade.register(clazz);
            }
            Object controller = blade.getBean(clazz);
            routeBuilder.addRouter(clazz, controller);
        }
        if (null != clazz.getAnnotation(Configuration.class) && clazz.getMethods().length > 0) {
            Object config = ReflectKit.newInstance(clazz);
            Arrays.stream(clazz.getMethods())
                    .filter(m -> m.getAnnotation(Bean.class) != null)
                    .forEach(n -> {
                        try {
                            blade.register(n.invoke(config));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
        }
        if (ReflectKit.hasInterface(clazz, WebHook.class) && null != clazz.getAnnotation(Bean.class)) {
            URLPattern URLPattern = clazz.getAnnotation(URLPattern.class);
            if (null == URLPattern) {
                routeBuilder.addWebHook(clazz, "/.*");
            } else {
                Stream.of(URLPattern.values())
                        .forEach(pattern -> routeBuilder.addWebHook(clazz, pattern));
            }
        }

        if (ReflectKit.hasInterface(clazz, BladeLoader.class) && null != clazz.getAnnotation(Bean.class)) {
            this.loaders.add((BladeLoader) blade.getBean(clazz));
        }
        if (ReflectKit.hasInterface(clazz, BeanProcessor.class) && null != clazz.getAnnotation(Bean.class)) {
            this.processors.add((BeanProcessor) blade.getBean(clazz));
        }
        if (isExceptionHandler(clazz)) {
            ExceptionHandler exceptionHandler = (ExceptionHandler) blade.getBean(clazz);
            blade.exceptionHandler(exceptionHandler);
        }
        WebSocket webSocket;
        if (null != (webSocket = clazz.getAnnotation(WebSocket.class))) {
            if (null == blade.getBean(clazz)) {
                blade.register(clazz);
            }
            if (ReflectKit.hasInterface(clazz, WebSocketHandler.class)) {
                blade.webSocket(webSocket.value(), (WebSocketHandler) blade.getBean(clazz));
            } else {
                WebSocketHandlerWrapper wrapper = blade.getBean(WebSocketHandlerWrapper.class);
                if (wrapper == null) {
                    wrapper = WebSocketHandlerWrapper.init(blade);
                    blade.register(wrapper);
                }
                blade.webSocket(webSocket.value(), wrapper);
                wrapper.wrapHandler(webSocket.value(), clazz);
            }

        }
    }

    private boolean isExceptionHandler(Class<?> clazz) {
        return (null != clazz.getAnnotation(Bean.class) && (
                ReflectKit.hasInterface(clazz, ExceptionHandler.class) || clazz.getSuperclass().equals(DefaultExceptionHandler.class)));
    }

    private void watchEnv() {
        boolean watchEnv = environment.getBoolean(ENV_KEY_APP_WATCH_ENV, false);

        if (watchEnv) {
            log.info("{}Watched environment started", getStartedSymbol());
            var thread = new Thread(new EnvironmentWatcher());
            thread.setName("watch@thread");
            thread.start();
        }
    }

    private void initConfig() {

        Optional.ofNullable(blade.bootClass())
                .map(Class::getPackage)
                .map(Package::getName)
                .ifPresent(blade::scanPackages);

        // print banner text
        this.printBanner();

        String statics = environment.get(ENV_KEY_STATIC_DIRS, "");
        if (StringKit.isNotBlank(statics)) {
            blade.addStatics(statics.split(","));
        }

        String templatePath = environment.get(ENV_KEY_TEMPLATE_PATH, "templates");
        if (templatePath.charAt(0) == HttpConst.CHAR_SLASH) {
            templatePath = templatePath.substring(1);
        }
        if (templatePath.endsWith(HttpConst.SLASH)) {
            templatePath = templatePath.substring(0, templatePath.length() - 1);
        }
        DefaultEngine.TEMPLATE_PATH = templatePath;
    }

    private void shutdownHook() {
        var shutdownThread = new Thread(this::stop);
        shutdownThread.setName("shutdown@thread");
        Runtime.getRuntime().addShutdownHook(shutdownThread);
    }

    @Override
    public void stop() {
        if (isStop) {
            return;
        }
        isStop = true;
        System.out.println();
        log.info("{}Blade shutdown ...", getStartedSymbol());
        try {
            WebContext.clean();
            if (this.bossGroup != null) {
                this.bossGroup.shutdownGracefully();
            }
            if (this.workerGroup != null) {
                this.workerGroup.shutdownGracefully();
            }
            log.info("{}Blade shutdown successful", getStartedSymbol());
        } catch (Exception e) {
            log.error("Blade shutdown error", e);
        }
    }

    @Override
    public void stopAndWait() {
        if (isStop) {
            return;
        }
        isStop = true;
        System.out.println();
        log.info("{}Blade shutdown ...", getStartedSymbol());
        try {
            if (this.bossGroup != null) {
                this.bossGroup.shutdownGracefully().sync();
            }
            if (this.workerGroup != null) {
                this.workerGroup.shutdownGracefully().sync();
            }
            log.info("{}Blade shutdown successful", getStartedSymbol());
        } catch (Exception e) {
            log.error("Blade shutdown error", e);
        }
    }

    @Override
    public void join() throws InterruptedException {
        channel.closeFuture().sync();
    }

    /**
     * print blade start banner text
     */
    private void printBanner() {
        if (null != blade.bannerText()) {
            System.out.println(blade.bannerText());
        } else {
            String text = Const.BANNER_TEXT + NEW_LINE +
                    StringKit.padLeft(" :: Blade :: (v", Const.BANNER_PADDING - 9) + Const.VERSION + ") " + NEW_LINE;
            System.out.println(Ansi.Magenta.format(text));
        }
    }

}
