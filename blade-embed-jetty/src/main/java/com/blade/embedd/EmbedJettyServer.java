package com.blade.embedd;

import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blade.Blade;
import com.blade.Const;
import com.blade.kit.Environment;
import com.blade.web.DispatcherServlet;

public class EmbedJettyServer implements EmbedServer {

	private static final Logger LOGGER = LoggerFactory.getLogger(EmbedJettyServer.class);
	
    private int port = Const.DEFAULT_PORT;
	
	private org.eclipse.jetty.server.Server server;
	
	private ServletContextHandler context;
	
	private Environment environment = null;
    
	public EmbedJettyServer() {
		Blade.$().loadAppConf("jetty.properties");
		environment = Blade.$().environment();
		Blade.$().enableServer(true);
	}
	
	@Override
	public void startup(int port) throws Exception {
		this.startup(port, Const.DEFAULT_CONTEXTPATH, null);
	}

	@Override
	public void startup(int port, String contextPath) throws Exception {
		this.startup(port, contextPath, null);
	}
	
	@Override
	public void startup(int port, String contextPath, String webRoot) throws Exception {
		this.port = port;
		
		// Setup Threadpool
        QueuedThreadPool threadPool = new QueuedThreadPool();
        
        int maxThreads = environment.getInt("server.jetty.max-threads", 100);
        
        threadPool.setMaxThreads(maxThreads);
        
		server = new org.eclipse.jetty.server.Server(threadPool);
		// 设置在JVM退出时关闭Jetty的钩子。
        server.setStopAtShutdown(true);
        
	    context = new ServletContextHandler(ServletContextHandler.SESSIONS);
	    context.setContextPath(contextPath);
	    
	    /*if(StringKit.isNotBlank(webRoot)){
	    	context.setResourceBase(webRoot);
	    } else{
	    	context.setResourceBase(getClass().getResource("").getPath());
	    }*/
	    context.setResourceBase(".");
	    
	    int securePort = environment.getInt("server.jetty.http.secure-port", 8443);
	    int outputBufferSize = environment.getInt("server.jetty.http.output-buffersize", 32768);
	    int requestHeaderSize = environment.getInt("server.jetty.http.request-headersize", 8192);
	    int responseHeaderSize = environment.getInt("server.jetty.http.response-headersize", 8192);
	    
	    // HTTP Configuration
        HttpConfiguration http_config = new HttpConfiguration();
        http_config.setSecurePort(securePort);
        http_config.setOutputBufferSize(outputBufferSize);
        http_config.setRequestHeaderSize(requestHeaderSize);
        http_config.setResponseHeaderSize(responseHeaderSize);
        http_config.setSendServerVersion(true);
        http_config.setSendDateHeader(false);
        
        long idleTimeout = environment.getLong("server.jetty.http.idle-timeout", 30000L);
        
        ServerConnector http = new ServerConnector(server, new HttpConnectionFactory(http_config));
        http.setPort(this.port);
        http.setIdleTimeout(idleTimeout);
        server.addConnector(http);
	    
	    ServletHolder servletHolder = new ServletHolder(DispatcherServlet.class);
	    servletHolder.setAsyncSupported(false);
	    servletHolder.setInitOrder(1);
	    
	    context.addServlet(servletHolder, "/");
        server.setHandler(this.context);
        
	    server.start();
	    LOGGER.info("Blade Server Listen on 0.0.0.0:{}", this.port);
	    server.join();
	}
	
    public void stop() throws Exception {
        server.stop();
    }
    
    public void waitForInterrupt() throws InterruptedException {
        server.join();
    }

}