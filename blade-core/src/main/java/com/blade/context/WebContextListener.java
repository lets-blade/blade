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
package com.blade.context;

import static com.blade.Blade.$;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blade.Blade;
import com.blade.Const;
import com.blade.banner.BannerStarter;
import com.blade.embedd.EmbedServer;
import com.blade.ioc.IocApplication;
import com.blade.kit.DispatchKit;
import com.blade.kit.SystemKit;

/**
 * Blade Web Context Listener
 * 
 * @author <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.6.7
 */
public class WebContextListener implements ServletContextListener, HttpSessionListener {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(WebContextListener.class);
	
	@Override
	public void sessionCreated(HttpSessionEvent event) {
		// session time out, default is 15 minutes, unit is minutes
		int timeout = $().config().getInt("server.timeout", 15);
		event.getSession().setMaxInactiveInterval(timeout * 60);
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		
		System.out.println("WebContextListener >>>>> contextInitialized");
		Blade blade = Blade.$();
		if(!blade.isInit()){
			
			ServletContext servletContext = sce.getServletContext();
			
			WebContextHolder.init(servletContext);
			
			LOGGER.info("jdk.version\t=> {}", SystemKit.getJavaInfo().getVersion());
			LOGGER.info("user.dir\t\t=> {}", System.getProperty("user.dir"));
			LOGGER.info("java.io.tmpdir\t=> {}", System.getProperty("java.io.tmpdir"));
			LOGGER.info("user.timezone\t=> {}", System.getProperty("user.timezone"));
			LOGGER.info("file.encodin\t=> {}", System.getProperty("file.encoding"));
			
			long initStart = System.currentTimeMillis();
			
			String webRoot = DispatchKit.getWebRoot(servletContext);
			
		    blade.webRoot(webRoot);
		    EmbedServer embedServer = blade.embedServer();
		    if(null != embedServer){
		    	embedServer.setWebRoot(webRoot);
		    }
		    
		    LOGGER.info("blade.webroot\t=> {}", webRoot);
		    
		    try {
				if(!blade.isInit()){
					if(!blade.applicationConfig().isInit()){
					    blade.loadAppConf(Const.APP_PROPERTIES);
						blade.applicationConfig().setEnv(blade.config());
				    }
					
					// initialization ioc
					IocApplication iocApplication = new IocApplication();
					iocApplication.initBeans();
					
					blade.init();
				}
				
				LOGGER.info("blade.isDev = {}", blade.isDev());
				
			    BannerStarter.printStart();
			    String appName = blade.config().get("app.name", "Blade");
			    LOGGER.info("{} initialize successfully, Time elapsed: {} ms.", appName, System.currentTimeMillis() - initStart);
			} catch (Exception e) {
				LOGGER.error("ApplicationContext init error", e);
			}
		}
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}
	
	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
	}

}