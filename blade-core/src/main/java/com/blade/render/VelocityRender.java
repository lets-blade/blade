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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Properties;
import java.util.Set;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import com.blade.Blade;
import com.blade.context.BladeWebContext;
import com.blade.web.http.Request;
import com.blade.web.http.wrapper.Session;

import blade.kit.log.Logger;

/**
 * 
 * <p>
 * Velocity渲染引擎
 * </p>
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class VelocityRender implements Render {
    
	private static final Logger LOGGER = Logger.getLogger(VelocityRender.class);
	
	private final VelocityEngine velocityEngine;
    
	private Blade blade;
	/**
	 * 默认构造函数
	 */
	public VelocityRender() {
		blade = Blade.me();
		Properties properties = new Properties();
		
		properties.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, blade.webRoot());
		properties.setProperty(Velocity.ENCODING_DEFAULT, blade.encoding());
		properties.setProperty(Velocity.INPUT_ENCODING, blade.encoding());
		properties.setProperty(Velocity.OUTPUT_ENCODING, blade.encoding());
        velocityEngine = new VelocityEngine(properties);
	}
	
	/**
	 * 根据配置文件构造一个Velocity引擎
	 * 
	 * @param propertiesFile	properties文件路径
	 * @throws IOException 		抛出IO异常
	 */
	public VelocityRender(String propertiesFile) throws IOException {
		blade = Blade.me();
		String loadPath = VelocityRender.class.getClassLoader().getResource("/").getPath();
		String fileName = loadPath + propertiesFile;
		
		Properties properties = new Properties();
		InputStream inStream = new FileInputStream(new File(fileName));
		properties.load(inStream);
		
		// 默认查询路径
		if(!properties.contains(Velocity.FILE_RESOURCE_LOADER_PATH)){
			properties.put(Velocity.FILE_RESOURCE_LOADER_PATH, blade.webRoot());
		}
        velocityEngine = new VelocityEngine(properties);
	}
	
	/**
	 * 根据构造一个Velocity引擎
	 * 
	 * @param properties	Properties配置文件
	 */
	public VelocityRender(Properties properties) {
		blade = Blade.me();
        velocityEngine = new VelocityEngine(properties);
	}
	
	/**
	 * 手动构造Velocity引擎
	 * 
	 * @param velocityEngine	velocity引擎对象
	 */
	public VelocityRender(VelocityEngine velocityEngine) {
		blade = Blade.me();
		this.velocityEngine = velocityEngine;
	}
	
	@Override
	public void render(ModelAndView modelAndView, Writer writer) throws RenderException {
		Request request = BladeWebContext.request();
		Session session = request.session();
		
		VelocityContext context = new VelocityContext(modelAndView.getModel());
		
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
			Template template = velocityEngine.getTemplate(modelAndView.getView());
			template.merge(context, writer);
			writer.flush(); 
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			LOGGER.error(e);
		}
		
	}
	
}
