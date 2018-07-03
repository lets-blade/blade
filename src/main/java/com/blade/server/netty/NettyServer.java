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
import com.blade.event.EventType;
import com.blade.ioc.DynamicContext;
import com.blade.ioc.annotation.Bean;
import com.blade.ioc.annotation.Value;
import com.blade.ioc.bean.ClassInfo;
import com.blade.ioc.bean.OrderComparator;
import com.blade.kit.*;
import com.blade.loader.BladeLoader;
import com.blade.mvc.Const;
import com.blade.mvc.WebContext;
import com.blade.mvc.annotation.Path;
import com.blade.mvc.annotation.URLPattern;
import com.blade.mvc.handler.DefaultExceptionHandler;
import com.blade.mvc.handler.ExceptionHandler;
import com.blade.mvc.hook.WebHook;
import com.blade.mvc.http.session.SessionCleaner;
import com.blade.mvc.route.RouteBuilder;
import com.blade.mvc.ui.template.DefaultEngine;
import com.blade.server.Server;
import com.blade.task.Task;
import com.blade.task.TaskContext;
import com.blade.task.TaskManager;
import com.blade.task.TaskStruct;
import com.blade.task.cron.CronExecutorService;
import com.blade.task.cron.CronExpression;
import com.blade.task.cron.CronThreadPoolExecutor;
import com.blade.watcher.EnvironmentWatcher;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.util.ResourceLeakDetector;
import lombok.extern.slf4j.Slf4j;
import lombok.var;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
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
    private EventLoopGroup      workerGroup;
    private Channel             channel;
    private RouteBuilder        routeBuilder;
    private List<BeanProcessor> processors;
    private List<BladeLoader>   loaders;
    private List<TaskStruct>    taskStruts = new ArrayList<>();

    private final int padSize = 26;

    private volatile boolean isStop;

    @Override
    public void start(Blade blade, String[] args) throws Exception {
        this.blade = blade;
        this.environment = blade.environment();
        this.processors = blade.processors();
        this.loaders = blade.loaders();

        var startMs = System.currentTimeMillis();
        log.info("{} {}{}", StringKit.padRight("environment.jdk.version", padSize), getPrefixSymbol(), System.getProperty("java.version"));
        log.info("{} {}{}", StringKit.padRight("environment.user.dir", padSize), getPrefixSymbol(), System.getProperty("user.dir"));
        log.info("{} {}{}", StringKit.padRight("environment.java.io.tmpdir", padSize), getPrefixSymbol(), System.getProperty("java.io.tmpdir"));
        log.info("{} {}{}", StringKit.padRight("environment.user.timezone", padSize), getPrefixSymbol(), System.getProperty("user.timezone"));
        log.info("{} {}{}", StringKit.padRight("environment.file.encoding", padSize), getPrefixSymbol(), System.getProperty("file.encoding"));
        log.info("{} {}{}", StringKit.padRight("environment.classpath", padSize), getPrefixSymbol(), CLASSPATH);

        this.initConfig();

        var contextPath = environment.get(ENV_KEY_CONTEXT_PATH, "/");
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
            Thread sessionCleanerThread = new Thread(new SessionCleaner(blade.sessionManager()));
            sessionCleanerThread.setName("session-cleaner");
            sessionCleanerThread.start();
        }
    }

    private void initIoc() {
        var routeMatcher = blade.routeMatcher();
        routeMatcher.initMiddleware(blade.middleware());

        routeBuilder = new RouteBuilder(routeMatcher);

        blade.scanPackages().stream()
                .flatMap(DynamicContext::recursionFindClasses)
                .map(ClassInfo::getClazz)
                .filter(ReflectKit::isNormalClass)
                .forEach(this::parseCls);

        routeMatcher.register();

        this.loaders.stream().sorted(new OrderComparator<>()).forEach(b -> b.preLoad(blade));
        this.processors.stream().sorted(new OrderComparator<>()).forEach(b -> b.preHandle(blade));

        var ioc = blade.ioc();
        if (BladeKit.isNotEmpty(ioc.getBeans())) {
            log.info("{}Register bean: {}", getStartedSymbol(), ioc.getBeans());
        }

        var beanDefines = ioc.getBeanDefines();

        if (BladeKit.isNotEmpty(beanDefines)) {
            beanDefines.forEach(b -> {
                BladeKit.injection(ioc, b);
                BladeKit.injectionValue(environment, b);
                var cronExpressions = BladeKit.getTasks(b.getType());
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

        var SSL = environment.getBoolean(ENV_KEY_SSL, false);
        // Configure SSL.
        SslContext sslCtx = null;
        if (SSL) {
            var certFilePath       = environment.get(ENV_KEY_SSL_CERT, null);
            var privateKeyPath     = environment.get(ENE_KEY_SSL_PRIVATE_KEY, null);
            var privateKeyPassword = environment.get(ENE_KEY_SSL_PRIVATE_KEY_PASS, null);

            log.info("{}SSL CertChainFile  Path: {}", getStartedSymbol(), certFilePath);
            log.info("{}SSL PrivateKeyFile Path: {}", getStartedSymbol(), privateKeyPath);
            sslCtx = SslContextBuilder.forServer(new File(certFilePath), new File(privateKeyPath), privateKeyPassword).build();
        }

        // Configure the server.
        var backlog = environment.getInt(ENV_KEY_NETTY_SO_BACKLOG, DEFAULT_SO_BACKLOG);

        var bootstrap = new ServerBootstrap();
        bootstrap.option(ChannelOption.SO_BACKLOG, backlog);
        bootstrap.option(ChannelOption.SO_REUSEADDR, true);
        bootstrap.childOption(ChannelOption.SO_REUSEADDR, true);

        int acceptThreadCount = environment.getInt(ENC_KEY_NETTY_ACCEPT_THREAD_COUNT, DEFAULT_ACCEPT_THREAD_COUNT);
        int ioThreadCount     = environment.getInt(ENV_KEY_NETTY_IO_THREAD_COUNT, DEFAULT_IO_THREAD_COUNT);

        // enable epoll
        if (BladeKit.epollIsAvailable()) {
            log.info("{}Use EpollEventLoopGroup", getStartedSymbol());
            bootstrap.option(EpollChannelOption.SO_REUSEPORT, true);

            var nettyServerGroup = EpollKit.group(acceptThreadCount, ioThreadCount);
            this.bossGroup = nettyServerGroup.getBoosGroup();
            this.workerGroup = nettyServerGroup.getWorkerGroup();
            bootstrap.group(bossGroup, workerGroup).channel(nettyServerGroup.getSocketChannel());
        } else {
            log.info("{}Use NioEventLoopGroup", getStartedSymbol());

            this.bossGroup = new NioEventLoopGroup(acceptThreadCount, new NamedThreadFactory("boss@"));
            this.workerGroup = new NioEventLoopGroup(ioThreadCount, new NamedThreadFactory("worker@"));
            bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class);
        }

        bootstrap.handler(new LoggingHandler(LogLevel.DEBUG))
                .childHandler(new HttpServerInitializer(sslCtx, blade, bossGroup.next()));

        var address = environment.get(ENV_KEY_SERVER_ADDRESS, DEFAULT_SERVER_ADDRESS);
        var port    = environment.getInt(ENV_KEY_SERVER_PORT, DEFAULT_SERVER_PORT);

        channel = bootstrap.bind(address, port).sync().channel();

        var appName = environment.get(ENV_KEY_APP_NAME, "Blade");
        var url     = Ansi.BgRed.and(Ansi.Black).format(" %s:%d ", address, port);

        var protocol = SSL ? "https" : "http";

        log.info("{}{} initialize successfully, Time elapsed: {} ms", getStartedSymbol(), appName, (System.currentTimeMillis() - startMs));
        log.info("{}Blade start with {}", getStartedSymbol(), url);
        log.info("{}Open browser access {}://{}:{} ⚡\r\n", getStartedSymbol(), protocol, address.replace(DEFAULT_SERVER_ADDRESS, LOCAL_IP_ADDRESS), port);

        blade.eventManager().fireEvent(EventType.SERVER_STARTED, blade);
    }

    private void startTask() {
        if (taskStruts.size() > 0) {
            int                 corePoolSize    = environment.getInt(ENV_KEY_TASK_THREAD_COUNT, Runtime.getRuntime().availableProcessors() + 1);
            CronExecutorService executorService = TaskManager.getExecutorService();
            if (null == executorService) {
                executorService = new CronThreadPoolExecutor(corePoolSize, new NamedThreadFactory("task@"));
                TaskManager.init(executorService);
            }

            var jobCount = new AtomicInteger();
            for (var taskStruct: taskStruts) {
                addTask(executorService, jobCount, taskStruct);
            }
        }
    }

    private void addTask(CronExecutorService executorService, AtomicInteger jobCount, TaskStruct taskStruct) {
        try {
            var schedule = taskStruct.getSchedule();
            var jobName  = StringKit.isBlank(schedule.name()) ? "task-" + jobCount.getAndIncrement() : schedule.name();
            var task     = new Task(jobName, new CronExpression(schedule.cron()), schedule.delay());

            var taskContext = new TaskContext(task);

            task.setTask(() -> {
                var target = blade.ioc().getBean(taskStruct.getType());
                var method = taskStruct.getMethod();
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

            var future = executorService.submit(task);
            task.setFuture(future);
            TaskManager.addTask(task);
        } catch (Exception e) {
            log.warn("{}Add task fail: {}", getPrefixSymbol(), e.getMessage());
        }
    }

    private void parseCls(Class<?> clazz) {
        if (null != clazz.getAnnotation(Bean.class) || null != clazz.getAnnotation(Value.class)) {
            blade.register(clazz);
        }
        if (null != clazz.getAnnotation(Path.class)) {
            if (null == blade.ioc().getBean(clazz)) {
                blade.register(clazz);
            }
            var controller = blade.ioc().getBean(clazz);
            routeBuilder.addRouter(clazz, controller);
        }
        if (ReflectKit.hasInterface(clazz, WebHook.class) && null != clazz.getAnnotation(Bean.class)) {
            var        hook       = blade.ioc().getBean(clazz);
            URLPattern URLPattern = clazz.getAnnotation(URLPattern.class);
            if (null == URLPattern) {
                routeBuilder.addWebHook(clazz, "/.*", hook);
            } else {
                Stream.of(URLPattern.values())
                        .forEach(pattern -> routeBuilder.addWebHook(clazz, pattern, hook));
            }
        }

        if (ReflectKit.hasInterface(clazz, BladeLoader.class) && null != clazz.getAnnotation(Bean.class)) {
            this.loaders.add((BladeLoader) blade.ioc().getBean(clazz));
        }
        if (ReflectKit.hasInterface(clazz, BeanProcessor.class) && null != clazz.getAnnotation(Bean.class)) {
            this.processors.add((BeanProcessor) blade.ioc().getBean(clazz));
        }
        if (isExceptionHandler(clazz)) {
            var exceptionHandler = (ExceptionHandler) blade.ioc().getBean(clazz);
            blade.exceptionHandler(exceptionHandler);
        }
    }

    private boolean isExceptionHandler(Class<?> clazz) {
        return (null != clazz.getAnnotation(Bean.class) && (
                ReflectKit.hasInterface(clazz, ExceptionHandler.class) || clazz.getSuperclass().equals(DefaultExceptionHandler.class)));
    }

    private void watchEnv() {
        var watchEnv = environment.getBoolean(ENV_KEY_APP_WATCH_ENV, true);
        log.info("{}Watched environment: {}", getStartedSymbol(), watchEnv, getStartedSymbol());

        if (watchEnv) {
            var thread = new Thread(new EnvironmentWatcher());
            thread.setName("watch@thread");
            thread.start();
        }
    }

    private void initConfig() {

        if (null != blade.bootClass()) {
            blade.scanPackages(blade.bootClass().getPackage().getName());
        }

        // print banner text
        this.printBanner();

        var statics = environment.get(ENV_KEY_STATIC_DIRS, "");
        if (StringKit.isNotBlank(statics)) {
            blade.addStatics(statics.split(","));
        }

        var templatePath = environment.get(ENV_KEY_TEMPLATE_PATH, "templates");
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
