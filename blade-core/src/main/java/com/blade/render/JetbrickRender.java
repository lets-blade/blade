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
package com.blade.render;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.blade.Blade;
import com.blade.context.BladeWebContext;
import com.blade.render.ModelAndView;
import com.blade.render.Render;
import com.blade.web.http.Request;
import com.blade.web.http.wrapper.Session;

import blade.kit.log.Logger;
import jetbrick.template.JetEngine;
import jetbrick.template.JetTemplate;

/**
 * Velocity Render
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class JetbrickRender implements Render {
    
	private static final Logger LOGGER = Logger.getLogger(JetbrickRender.class);
			
	private JetEngine jetEngine;
    
	private Properties config;
	
	private String webRoot;
	
	public JetbrickRender() {
		Blade blade = Blade.me();
		this.webRoot = blade.webRoot();
		config = new Properties();
		config.put("jetx.input.encoding", blade.encoding());
		config.put("jetx.output.encoding", blade.encoding());
		config.put("jetx.template.suffix", ".html");
		config.put("jetx.template.loaders", "jetbrick.template.loader.FileSystemResourceLoader");
		jetEngine = JetEngine.create(config);
	}
	
	/**
	 * @return	return JetEngine
	 */
	public JetEngine getJetEngine(){
		return jetEngine;
	}
	
	/**
	 * clean config
	 */
	public void clean(){
		if(null != config){
			config.clear();
		}
	}
	
	/**
	 * add config
	 * 
	 * @param key	key
	 * @param value	value
	 */
	public void put(String key, Object value){
		if(null == config){
			config = new Properties();
		}
		config.put(key, value);
	}
	
	/**
	 * Construct a JetEngine engine based on the configuration file 
	 * 
	 * @param configLocation	configuration file path for ClassPath
	 * @throws IOException 		io exception
	 */
	public JetbrickRender(String configLocation) throws IOException {
		Blade blade = Blade.me();
		this.webRoot = blade.webRoot();
		jetEngine = JetEngine.create(configLocation);
	}
	
	/**
	 * According to the construction of a JetEngine engine 
	 * 
	 * @param config	properties configuration 
	 */
	public JetbrickRender(Properties config) {
		Blade blade = Blade.me();
		this.webRoot = blade.webRoot();
		this.config = config;
		jetEngine = JetEngine.create(this.config);
	}
	
	/**
	 * Manually constructed JetEngine engine 
	 * 
	 * @param jetEngine	jetEngine engine object
	 */
	public JetbrickRender(JetEngine jetEngine) {
		this.jetEngine = jetEngine;
	}
	
	/**
	 * @return	return config object
	 */
	public Properties getConfig() {
		return config;
	}

	@Override
	public void render(ModelAndView modelAndView, Writer writer) {
		Request request = BladeWebContext.request();
		Session session = request.session();
		
		String view = webRoot + modelAndView.getView();
		
		JetTemplate template = jetEngine.getTemplate(view);
		
		Map<String, Object> context = modelAndView.getModel();
		
		Set<String> attrs = request.attributes();
		if(null != attrs && attrs.size() > 0){
			for(String attr : attrs){
				context.put(attr, request.attribute(attr));
			}
		}
		
		Set<String> session_attrs = session.attributes();
		if(null != session_attrs && session_attrs.size() > 0){
			for(String attr : session_attrs){
				context.put(attr, session.attribute(attr));
			}
		}
		
		try {
			template.render(context, writer);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e);
		}
	}
	
}
