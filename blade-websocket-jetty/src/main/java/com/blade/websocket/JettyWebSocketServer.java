package com.blade.websocket;

import javax.websocket.server.ServerContainer;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;

public class JettyWebSocketServer implements WebSocketServer {
	
	private Server server;
	private ServletContextHandler context;
	private ServerContainer wscontainer;
	private Class<?>[] endPoints;

	@Override
	public void start(int port) throws WebSocketException{
		this.start(port, "/");
	}
	
	@Override
	public void start(int port, String contextPath) throws WebSocketException{
		server = new Server();
		ServerConnector connector = new ServerConnector(server);
		connector.setPort(port);
		server.addConnector(connector);

		// Setup the basic application "context" for this application at "/"
		// This is also known as the handler tree (in jetty speak)
		context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath(contextPath);
		server.setHandler(context);
		try {
			// Initialize javax.websocket layer
			wscontainer = WebSocketServerContainerInitializer.configureContext(context);
			if (null != endPoints) {
				// Add WebSocket endpoint to javax.websocket layer
				for (int i = 0; i < endPoints.length; i++) {
					wscontainer.addEndpoint(endPoints[i]);
				}
			}
			server.start();
			server.dump(System.err);
			server.join();
		} catch (Throwable t) {
			 throw new WebSocketException(t);
		}
	}

	@Override
	public void stop() throws WebSocketException{
		try {
			server.stop();
		} catch (Exception e) {
			throw new WebSocketException(e);
		}
	}

	@Override
	public void addEndpoints(Class<?>... endPoints) {
		this.endPoints = endPoints;
	}

}
