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
import java.util.Set;

import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.exception.BeetlException;
import org.beetl.core.resource.WebAppResourceLoader;

import com.blade.Blade;
import com.blade.context.BladeWebContext;
import com.blade.http.Request;
import com.blade.servlet.Session;

import blade.kit.log.Logger;

/**
 * 
 * <p>
 * Beetl渲染引擎
 * </p>
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class BeetlRender implements Render {
    
	private static final Logger LOGGER = Logger.getLogger(BeetlRender.class);
	
	/**
	 * Beetl获取模板对象
	 */
	private GroupTemplate groupTemplate = null;
	
	/**
	 * Blade对象
	 */
	private Blade blade;
	
	/**
	 * 默认构造函数
	 */
	public BeetlRender() {
		try {
			blade = Blade.me();
			String root = blade.webRoot() + blade.viewPrefix();
			WebAppResourceLoader resourceLoader = new WebAppResourceLoader();
			resourceLoader.setAutoCheck(true);
			resourceLoader.setRoot(root);
			Configuration cfg = Configuration.defaultConfiguration();
			groupTemplate = new GroupTemplate(resourceLoader, cfg);
		} catch (IOException e) {
			LOGGER.error(e);
		}
	}
	
	public BeetlRender(Configuration configuration) {
		try {
			blade = Blade.me();
			String root = blade.webRoot() + blade.viewPrefix();
			WebAppResourceLoader resourceLoader = new WebAppResourceLoader();
			resourceLoader.setAutoCheck(true);
			resourceLoader.setRoot(root);
			Configuration cfg = Configuration.defaultConfiguration();
			groupTemplate = new GroupTemplate(resourceLoader, cfg);
			groupTemplate.setConf(configuration);
		} catch (IOException e) {
			LOGGER.error(e);
		}
	}
	
	@Override
	public void render(ModelAndView modelAndView, Writer writer) throws RenderException {
		Request request = BladeWebContext.request();
		Session session = request.session();
		
		Template template = groupTemplate.getTemplate(modelAndView.getView());
		
		Map<String, Object> context = modelAndView.getModel();
		if(null != context && context.size() > 0){
			Set<String> keys = context.keySet();
			for(String key : keys){
				template.binding(key, context.get(key));
			}
		}
		
		Set<String> attrs = request.attributes();
		if(null != attrs && attrs.size() > 0){
			for(String attr : attrs){
				template.binding(attr, request.attribute(attr));
			}
		}
		
		Set<String> session_attrs = session.attributes();
		if(null != session_attrs && session_attrs.size() > 0){
			for(String attr : session_attrs){
				template.binding(attr, session.attribute(attr));
			}
		}
		try {
			template.renderTo(writer);
		} catch (BeetlException e) {
			e.printStackTrace();
			LOGGER.error(e);
		}
	}
}
