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
package com.blade.kit.resource;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blade.kit.Assert;
import com.blade.kit.CollectionKit;

/**
 * 根据jar文件读取类
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class JarReaderImpl extends AbstractClassReader implements ClassReader {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(JarReaderImpl.class);
	
	@Override
	public Set<ClassInfo> getClass(String packageName, boolean recursive) {
		return this.getClassByAnnotation(packageName, null, null, recursive);
	}

	@Override
	public Set<ClassInfo> getClass(String packageName, Class<?> parent, boolean recursive) {
		return this.getClassByAnnotation(packageName, parent, null, recursive);
	}

	@Override
	public Set<ClassInfo> getClassByAnnotation(String packageName, Class<? extends Annotation> annotation, boolean recursive) {
		return this.getClassByAnnotation(packageName, null, annotation, recursive);
	}

	@Override
	public Set<ClassInfo> getClassByAnnotation(String packageName, Class<?> parent, Class<? extends Annotation> annotation, boolean recursive) {
		Assert.notBlank(packageName);
		Set<ClassInfo> classes = CollectionKit.newHashSet();
        // 获取包的名字 并进行替换
        String packageDirName = packageName.replace('.', '/');
        // 定义一个枚举的集合 并进行循环来处理这个目录下的URL
        Enumeration<URL> dirs;
        try {
            dirs = this.getClass().getClassLoader().getResources(packageDirName);
            // 循环迭代下去
            while (dirs.hasMoreElements()) {
                // 获取下一个元素
                URL url = dirs.nextElement();
				Set<ClassInfo> subClasses = this.getClasses(url, packageDirName, packageName, parent, annotation, recursive, classes);
				if(subClasses.size() > 0){
					classes.addAll(subClasses);
				}
            }
        } catch (IOException e) {
        	LOGGER.error(e.getMessage(), e);
        }
        return classes;
	}
	
	private Set<ClassInfo> getClasses(final URL url, final String packageDirName, String packageName, final Class<?> parent, 
			final Class<? extends Annotation> annotation, final boolean recursive, Set<ClassInfo> classes){
		try {
			if( url.toString().startsWith( "jar:file:" ) || url.toString().startsWith( "wsjar:file:" ) ) {
				
				// 获取jar
		        JarFile jarFile = ( (JarURLConnection)url.openConnection() ).getJarFile();

		        // 从此jar包 得到一个枚举类
		        Enumeration<JarEntry> eje = jarFile.entries();

		        // 同样的进行循环迭代
				while (eje.hasMoreElements()) {
					// 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
					JarEntry entry = eje.nextElement();
					String name = entry.getName();
					// 如果是以/开头的
					if (name.charAt(0) == '/') {
						// 获取后面的字符串
						name = name.substring(1);
					}
					// 如果前半部分和定义的包名相同
					if (name.startsWith(packageDirName)) {
						int idx = name.lastIndexOf('/');
						// 如果以"/"结尾 是一个包
						if (idx != -1) {
							// 获取包名 把"/"替换成"."
							packageName = name.substring(0, idx).replace('/', '.');
						}
						// 如果可以迭代下去 并且是一个包
						if ((idx != -1) || recursive) {
							// 如果是一个.class文件 而且不是目录
							if (name.endsWith(".class") && !entry.isDirectory()) {
								// 去掉后面的".class" 获取真正的类名
								String className = name.substring(packageName.length() + 1, name.length() - 6);
								// 添加到classes
									Class<?> clazz = Class.forName(packageName + '.' + className);
								if(null != parent && null != annotation){
									if(null != clazz.getSuperclass() && 
										clazz.getSuperclass().equals(parent) && null != clazz.getAnnotation(annotation)){
										classes.add(new ClassInfo(clazz));
									}
									continue;
								}
								if(null != parent){
									if(null != clazz.getSuperclass() && clazz.getSuperclass().equals(parent)){
										classes.add(new ClassInfo(clazz));
									}
									continue;
								}
								if(null != annotation){
									if(null != clazz.getAnnotation(annotation)){
										classes.add(new ClassInfo(clazz));
									}
									continue;
								}
								classes.add(new ClassInfo(clazz));
							}
						}
					}
				}
		    }
		} catch (IOException e) {
			LOGGER.error("The scan error when the user to define the view from a jar package file.", e);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return classes;
	}
}