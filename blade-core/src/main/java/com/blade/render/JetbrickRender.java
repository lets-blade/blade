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
 * 
 * <p>
 * Velocity渲染引擎
 * </p>
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class JetbrickRender implements Render {
    
	private static final Logger LOGGER = Logger.getLogger(JetbrickRender.class);
			
	private JetEngine jetEngine;
    
	private Properties config;
	
	private String webRoot;
	/**
	 * 默认构造函数
	 */
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
	 * @return	返回JetEngine引擎
	 */
	public JetEngine getJetEngine(){
		return jetEngine;
	}
	
	/**
	 * 清空配置
	 */
	public void clean(){
		if(null != config){
			config.clear();
		}
	}
	
	/**
	 * 添加一个配置
	 * 
	 * @param key	配置键
	 * @param value	配置值
	 */
	public void put(String key, Object value){
		if(null == config){
			config = new Properties();
		}
		config.put(key, value);
	}
	
	/**
	 * 根据配置文件构造一个JetEngine引擎
	 * 
	 * @param configLocation	配置文件路径
	 * @throws IOException 		抛出IO异常
	 */
	public JetbrickRender(String configLocation) throws IOException {
		Blade blade = Blade.me();
		this.webRoot = blade.webRoot();
		jetEngine = JetEngine.create(configLocation);
	}
	
	/**
	 * 根据构造一个JetEngine引擎
	 * 
	 * @param config	Properties配置
	 */
	public JetbrickRender(Properties config) {
		Blade blade = Blade.me();
		this.webRoot = blade.webRoot();
		this.config = config;
		jetEngine = JetEngine.create(this.config);
	}
	
	/**
	 * 手动构造JetEngine引擎
	 * 
	 * @param jetEngine	jetEngine引擎
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
