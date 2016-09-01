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

import com.blade.Blade;
import com.blade.kit.resource.ClassPathClassReader;
import com.blade.kit.resource.ClassReader;
import com.blade.kit.resource.JarReaderImpl;

/**
 * 动态根据环境获取ClassReader
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public final class DynamicClassReader {
	
	private static boolean IS_JAR_CONTEXT = false;
	
	private DynamicClassReader() {
	}
	
	public static void init(){
		/*String rs = DynamicClassReader.class.getResource("").toString();
		if(rs.indexOf(".jar!") != -1){
			IS_JAR_CONTEXT = true;
		}*/
		Class<?> clazz = Blade.$().config().getApplicationClass();
		String rs = clazz.getResource("").toString();
		if(rs.indexOf(".jar!") != -1){
			IS_JAR_CONTEXT = true;
		}
	}
	
	public static ClassReader getClassReader(){
		if(IS_JAR_CONTEXT){
			return new JarReaderImpl();
		}
		return new ClassPathClassReader();
	}
	
}
