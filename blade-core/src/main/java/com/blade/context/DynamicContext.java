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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blade.Blade;
import com.blade.kit.resource.ClassPathClassReader;
import com.blade.kit.resource.ClassReader;
import com.blade.kit.resource.JarReaderImpl;

/**
 * Get ClassReader by JAR or folder
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.6.6
 */
public final class DynamicContext {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DynamicContext.class);
	
	private static ClassReader CLASS_READER = null;
	
	private static boolean isJarContext = false;
	
	private DynamicContext() {
	}
	
	public static void init(){
		Class<?> clazz = Blade.$().config().getApplicationClass();
		String rs = clazz.getResource("").toString();
		if(rs.indexOf(".jar") != -1){
			CLASS_READER = new JarReaderImpl();
			isJarContext = true;
			LOGGER.debug("{}", CLASS_READER);
		} else{
			CLASS_READER = new ClassPathClassReader();
		}
	}
	
	public static ClassReader getClassReader(){
		return CLASS_READER;
	}
	
	public static boolean isJarContext() {
		return isJarContext;
	}
	
}