/**
 * Copyright (c) 2015, biezhi 王爵 (biezhi.me@gmail.com)
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
package blade.server;

import java.util.EnumSet;

import javax.servlet.DispatcherType;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.webapp.WebAppContext;

import blade.Blade;
import blade.BladeFilter;
import blade.log.Logger;

/**
 * 内置jetty服务
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public final class BladeServer {
	
	/**
	 * 默认的应用所在位置
	 */
	private static String DEFAULT_APP_PATH = BladeServer.class.getResource("/").toString();
	
	private static final Logger LOGGER = Logger.getLogger(BladeServer.class);
	
	private BladeServer(){
		
	}
	
	static{
		String result = BladeServer.class.getResource("/").toString();
		System.out.println(result);
		// maven项目
		/*if (result.indexOf("/target/classes/") != -1) {
			DEFAULT_APP_PATH = "src/main/webapp";
		}*/
	}
	
	/**
	 * 创建用于开发运行调试的Jetty Server,
	 */
	public static Server createServerInSource(Integer port, String host, String contextPath) {
		
		Server server = new Server();
		// 设置在JVM退出时关闭Jetty的钩子。
		server.setStopAtShutdown(true);

		// 这是http的连接器
		ServerConnector connector = new ServerConnector(server);
		connector.setPort(port);
		connector.setHost(host);
		
		// 解决Windows下重复启动Jetty居然不报告端口冲突的问题.
		connector.setReuseAddress(false);
		server.setConnectors(new Connector[] { connector });
		
		WebAppContext webContext = new WebAppContext(DEFAULT_APP_PATH, contextPath);
		
		webContext.addFilter(BladeFilter.class, "/*", EnumSet.of(DispatcherType.INCLUDE,DispatcherType.REQUEST,DispatcherType.FORWARD, DispatcherType.ASYNC));
		
		// 设置webapp的位置
		webContext.setResourceBase(DEFAULT_APP_PATH);
		webContext.setClassLoader(Thread.currentThread().getContextClassLoader());
		server.setHandler(webContext);
		return server;
	}
	
	public static void run(Integer port, String host, String contextPath){
		try {
			
			if(null == port){
				port = 9000;
			}
			
			if(null == host){
				host = "0.0.0.0";
			}
			
			if(null == contextPath){
				contextPath = "/";
			}
			
			final Server server = createServerInSource(port, host, contextPath);
			
			Blade.runJetty = true;
			server.stop();
			server.start();
			LOGGER.info("Blade Server Run In : http://" + host + ":" + port + contextPath);
			server.join();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
}