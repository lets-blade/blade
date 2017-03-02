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

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blade.kit.Assert;
import com.blade.kit.CollectionKit;
import com.blade.kit.exception.ClassReaderException;

/**
 * 抽象类读取器
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public abstract class AbstractClassReader implements ClassReader {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractClassReader.class);
	
	@Override
	public Set<ClassInfo> getClass(String packageName, boolean recursive) {
		return this.getClassByAnnotation(packageName, null, null, recursive);
	}

	/**
	 * 默认实现以文件形式的读取
	 */
	@Override
	public Set<ClassInfo> getClass(String packageName, Class<?> parent, boolean recursive) {
        return this.getClassByAnnotation(packageName, parent,  null, recursive);
	}
	
	/**
	 * 根据条件获取class
	 * @param packageName
	 * @param packagePath
	 * @param parent
	 * @param annotation
	 * @param recursive
	 * @return
	 */
	private Set<ClassInfo> findClassByPackage(final String packageName, final String packagePath, 
			final Class<?> parent,  final Class<? extends Annotation> annotation, 
			final boolean recursive, Set<ClassInfo> classes) throws ClassNotFoundException {
		
		// 获取此包的目录 建立一个File
        File dir = new File(packagePath);
        // 如果不存在或者 也不是目录就直接返回
        if ((!dir.exists()) || (!dir.isDirectory())) {
        	LOGGER.warn("The package [{}] not found.", packageName);
        }
        // 如果存在 就获取包下的所有文件 包括目录
        File[] dirfiles = accept(dir, recursive);
        // 循环所有文件
        if(null != dirfiles && dirfiles.length > 0){
        	for (File file : dirfiles) {
                // 如果是目录 则继续扫描
                if (file.isDirectory()) {
                	findClassByPackage(packageName + "." + file.getName(), file.getAbsolutePath(), parent, annotation, recursive, classes);
                } else {
                    // 如果是java类文件 去掉后面的.class 只留下类名
                    String className = file.getName().substring(0, file.getName().length() - 6);
//                    Class<?> clazz = classLoader.defineClassByName(packageName + '.' + className);
                    	Class<?> clazz = Class.forName(packageName + '.' + className);
					if(null != parent && null != annotation){
						if(null != clazz.getSuperclass() && clazz.getSuperclass().equals(parent) && 
								null != clazz.getAnnotation(annotation)){
							classes.add(new ClassInfo(clazz));
						}
						continue;
					}
					if(null != parent){
						if(null != clazz.getSuperclass() && clazz.getSuperclass().equals(parent)){
							classes.add(new ClassInfo(clazz));
						} else {
							if(null != clazz.getInterfaces() && clazz.getInterfaces().length > 0 && clazz.getInterfaces()[0].equals(parent)){
								classes.add(new ClassInfo(clazz));
							}
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
        return classes;
    }
	
	/**
	 * 过滤文件规则
	 * @param file
	 * @param recursive
	 * @return
	 */
	private File[] accept(File file, final boolean recursive){
		File[] dirfiles = file.listFiles(new FileFilter() {
            // 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
            public boolean accept(File file) {
                return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
            }
        });
		return dirfiles;
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
		    	try {
					// 获取包的物理路径
		    		String filePath = new URI(url.getFile()).getPath();
					Set<ClassInfo> subClasses = findClassByPackage(packageName, filePath, parent, annotation, recursive, classes);
					if(subClasses.size() > 0){
						classes.addAll(subClasses);
					}
				} catch (URISyntaxException e) {
					LOGGER.error(e.getMessage(), e);
				}  
            }
        } catch (IOException e) {
        	LOGGER.error(e.getMessage(), e);
        } catch (ClassNotFoundException e) {
        	LOGGER.error("Add user custom view class error Can't find such Class files.");
			throw new ClassReaderException(e);
		}
        return classes;
	}

}