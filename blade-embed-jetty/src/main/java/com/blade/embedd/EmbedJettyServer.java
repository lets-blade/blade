package com.blade.embedd;

import com.blade.Blade;
import com.blade.Const;
import com.blade.exception.EmbedServerException;
import com.blade.kit.CollectionKit;
import com.blade.kit.StringKit;
import com.blade.kit.base.Config;
import com.blade.mvc.context.BladeInitListener;
import com.blade.mvc.context.DynamicContext;
import com.blade.mvc.dispatch.AsyncDispatcherServlet;
import com.blade.mvc.dispatch.DispatcherServlet;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.Scanner;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.servlet.http.HttpServlet;
import java.io.File;
import java.net.URL;
import java.util.EnumSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static com.blade.Blade.$;

/**
 * Blade Jetty Server
 *
 * @author <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 0.0.8
 */
public class EmbedJettyServer implements EmbedServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmbedJettyServer.class);

    private int port = Const.DEFAULT_PORT;

    private Server server;

    private String classPath;

    private WebAppContext webAppContext;

    public EmbedJettyServer() {
        System.setProperty("org.apache.jasper.compiler.disablejsr199", "true");
        if (DynamicContext.isJarContext()) {
            URL url = EmbedJettyServer.class.getResource("/");
            this.classPath = url.getPath();
            LOGGER.info("add classpath: {}", classPath);
        }
        $().enableServer(true);
    }

    @Override
    public void startup(int port) throws EmbedServerException {
        this.startup(port, "/", null);
    }

    @Override
    public void startup(int port, String contextPath) throws EmbedServerException {
        this.startup(port, contextPath, null);
    }

    @Override
    public void setWebRoot(String webRoot) {
        webAppContext.setResourceBase(webRoot);
    }

    @Override
    public void startup(int port, String contextPath, String webRoot) throws EmbedServerException {
        this.port = port;

        Config config = Blade.$().config();

        int minThreads = config.getInt("server.jetty.min-threads", 8);
        int maxThreads = config.getInt("server.jetty.max-threads", 200);

        String poolName = config.get("server.jetty.pool-name", "blade-pool");

        // Setup Threadpool
        QueuedThreadPool threadPool = new QueuedThreadPool();
        threadPool.setMinThreads(minThreads);
        threadPool.setMaxThreads(maxThreads);
        threadPool.setName(poolName);

        this.server = new org.eclipse.jetty.server.Server(threadPool);

        this.webAppContext = new WebAppContext();

        this.webAppContext.setContextPath(contextPath);
        this.webAppContext.setResourceBase("");

        int securePort = config.getInt("server.jetty.http.secure-port", 9443);
        int outputBufferSize = config.getInt("server.jetty.http.output-buffersize", 32 * 1024);
        int requestHeaderSize = config.getInt("server.jetty.http.request-headersize", 8 * 1024);
        int responseHeaderSize = config.getInt("server.jetty.http.response-headersize", 8 * 1024);

        // HTTP Configuration
        HttpConfiguration http_config = new HttpConfiguration();
        http_config.setSecurePort(securePort);
        http_config.setOutputBufferSize(outputBufferSize);
        http_config.setRequestHeaderSize(requestHeaderSize);
        http_config.setResponseHeaderSize(responseHeaderSize);

        long idleTimeout = config.getLong("server.jetty.http.idle-timeout", 30000L);

        String host = config.get("server.host", "0.0.0.0");
        ServerConnector serverConnector = new ServerConnector(server, new HttpConnectionFactory(http_config));
        serverConnector.setHost(host);
        serverConnector.setPort(this.port);
        serverConnector.setIdleTimeout(idleTimeout);
        server.setConnectors(new Connector[]{serverConnector});

        boolean isAsync = config.getBoolean("server.async", false);

        Class<? extends Servlet> servlet = isAsync ? AsyncDispatcherServlet.class : DispatcherServlet.class;

        ServletHolder servletHolder = new ServletHolder(servlet);
        servletHolder.setAsyncSupported(isAsync);
        servletHolder.setInitOrder(1);

        webAppContext.addEventListener(new BladeInitListener());

        Set<String> statics = Blade.$().bConfig().getStatics();
        ServletHolder defaultHolder = new ServletHolder(DefaultServlet.class);
        defaultHolder.setInitOrder(0);
        if (StringKit.isNotBlank(classPath)) {
            LOGGER.info("add classpath : {}", classPath);
            defaultHolder.setInitParameter("resourceBase", classPath);
        }

        statics.forEach(s -> webAppContext.addServlet(defaultHolder, s + '*'));

        webAppContext.addServlet(servletHolder, "/");

        try {

            this.loadServlets(webAppContext);
            this.loadFilters(webAppContext);

            HandlerList handlerList = new HandlerList();
            handlerList.setHandlers(new Handler[]{webAppContext, new DefaultHandler()});
            server.setHandler(handlerList);
            server.setStopAtShutdown(true);

            server.start();
            LOGGER.info("Blade Server Listen on {}:{}", host, this.port);
            server.join();
        } catch (Exception e) {
            throw new EmbedServerException(e);
        }
    }

    public void hotSwap(int scanInterval, String resBase) throws Exception {
        Scanner scanner = new Scanner();
        scanner.setScanInterval(scanInterval);
        scanner.addScanDir(new File(resBase));
        scanner.addListener((Scanner.BulkListener) fileNames -> {
            webAppContext.stop();
            webAppContext.setResourceBase(resBase);
            webAppContext.start();
            webAppContext.getHandler().start();
        });
        LOGGER.info("Hot Swap scan interval is {}s.", scanInterval);
        scanner.start();
    }

    public void loadFilters(WebAppContext webAppContext) throws Exception {
        Map<Class<? extends Filter>, String[]> filters = Blade.$().filters();
        if (CollectionKit.isNotEmpty(filters)) {
            Set<Entry<Class<? extends Filter>, String[]>> entrySet = filters.entrySet();
            for (Entry<Class<? extends Filter>, String[]> entry : entrySet) {
                Class<? extends Filter> filterClazz = entry.getKey();
                String[] pathSpecs = entry.getValue();
                for (String pathSpec : pathSpecs) {
                    webAppContext.addFilter(filterClazz, pathSpec, EnumSet.of(DispatcherType.REQUEST));
                }
            }
        }
    }

    public void loadServlets(WebAppContext webAppContext) throws Exception {
        Map<Class<? extends HttpServlet>, String[]> servlets = Blade.$().servlets();
        if (CollectionKit.isNotEmpty(servlets)) {
            Set<Entry<Class<? extends HttpServlet>, String[]>> entrySet = servlets.entrySet();
            for (Entry<Class<? extends HttpServlet>, String[]> entry : entrySet) {
                Class<? extends HttpServlet> servletClazz = entry.getKey();
                String[] pathSpecs = entry.getValue();
                for (String pathSpec : pathSpecs) {
                    webAppContext.addServlet(servletClazz, pathSpec);
                }
            }
        }
    }

    public void shutdown() throws EmbedServerException {
        try {
            server.stop();
        } catch (Exception e) {
            throw new EmbedServerException(e);
        }
    }

    @Override
    public void join() throws EmbedServerException {
        try {
            server.join();
        } catch (InterruptedException e) {
            throw new EmbedServerException(e);
        }
    }

}