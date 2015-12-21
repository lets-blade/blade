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
 * Velocity Render
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class VelocityRender implements Render {
    
	private static final Logger LOGGER = Logger.getLogger(VelocityRender.class);
	
	private final VelocityEngine velocityEngine;
    
	private String webRoot;
	
	public VelocityRender() {
		Blade blade = Blade.me();
		this.webRoot = blade.webRoot();
		Properties properties = new Properties();
		
		properties.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, blade.webRoot());
		properties.setProperty(Velocity.ENCODING_DEFAULT, blade.encoding());
		properties.setProperty(Velocity.INPUT_ENCODING, blade.encoding());
		properties.setProperty(Velocity.OUTPUT_ENCODING, blade.encoding());
        velocityEngine = new VelocityEngine(properties);
	}
	
	/**
	 * Construct a Velocity engine based on the configuration file 
	 * 
	 * @param propertiesFile	properties file path
	 * @throws IOException 		IO Exception
	 */
	public VelocityRender(String propertiesFile) throws IOException {
		Blade blade = Blade.me();
		this.webRoot = blade.webRoot();
		String loadPath = VelocityRender.class.getClassLoader().getResource("/").getPath();
		String fileName = loadPath + propertiesFile;
		
		Properties properties = new Properties();
		InputStream inStream = new FileInputStream(new File(fileName));
		properties.load(inStream);
		
		// Default query path
		if(!properties.contains(Velocity.FILE_RESOURCE_LOADER_PATH)){
			properties.put(Velocity.FILE_RESOURCE_LOADER_PATH, blade.webRoot());
		}
        velocityEngine = new VelocityEngine(properties);
	}
	
	/**
	 * According to the construction of a Velocity engine 
	 * 
	 * @param properties	Properties配置文件
	 */
	public VelocityRender(Properties properties) {
		Blade blade = Blade.me();
		this.webRoot = blade.webRoot();
        velocityEngine = new VelocityEngine(properties);
	}
	
	/**
	 * Manually constructed Velocity engine 
	 * 
	 * @param velocityEngine	velocity engine object
	 */
	public VelocityRender(VelocityEngine velocityEngine) {
		Blade blade = Blade.me();
		this.webRoot = blade.webRoot();
		this.velocityEngine = velocityEngine;
	}
	
	@Override
	public void render(ModelAndView modelAndView, Writer writer) throws RenderException {
		Request request = BladeWebContext.request();
		Session session = request.session();
		
		String viewPath = webRoot + modelAndView.getView();
		
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
			Template template = velocityEngine.getTemplate(viewPath);
			template.merge(context, writer);
			writer.flush(); 
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			LOGGER.error(e);
		}
		
	}
	
}
