/**
 * Copyright (c) 2016, biezhi 王爵 (biezhi.me@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blade.websocket;

import javax.websocket.server.ServerContainer;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;

/**
 * JettyWebSocketServer
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.6.7
 */
public class JettyWebSocketServer implements WebSocketServer {
	
	private Server server;
	private ServletContextHandler context;
	private ServerContainer wscontainer;
	private Class<?>[] endPoints;

	public JettyWebSocketServer(Class<?>...endPoints) {
		this.endPoints = endPoints;
	}
	
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
	
	@Override
	public void join() throws WebSocketException {
		try {
			server.join();
		} catch (InterruptedException e) {
			throw new WebSocketException(e);
		}
	}

}
