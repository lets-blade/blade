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
package blade;

import java.util.Set;

import blade.BladeBase.PackageNames;
import blade.ioc.Container;
import blade.ioc.DefaultContainer;
import blade.kit.log.Logger;
import blade.kit.resource.ClassPathClassReader;
import blade.kit.resource.ClassReader;

/**
 * IOC容器初始化类
 * 
 * @author	biezhi
 * @since	1.0
 */
public final class IOCApplication {

	private static final Logger LOGGER = Logger.getLogger(IOCApplication.class);
	
	/**
	 * IOC容器，单例获取默认的容器实现
	 */
	static final Container container = DefaultContainer.single();
	
	/**
	 * 类读取对象，加载class
	 */
	static final ClassReader classReader = new ClassPathClassReader();
	
	/**
	 * 初始化IOC容器，加载ioc包的对象
	 * 要配置符合ioc的注解的类才会被加载
	 * 
	 */
	public static void init(){
		
		String[] iocPackages = BladeBase.packageMap.get(PackageNames.ioc);
		if(null != iocPackages && iocPackages.length > 0){
			for(String packageName : iocPackages){
				registerBean(packageName);
			}
			// 初始化注入
			container.initWired();
		}
	}
	
	/**
	 * 注册一个包下的所有对象
	 * 
	 * @param packageName 包名称
	 */
	private static void registerBean(String packageName) {
		
		// 是否递归扫描
		boolean recursive = false; 
		if (packageName.endsWith(".*")) {
			packageName = packageName.substring(0, packageName.length() - 2);
			recursive = true;
		}
		
		// 扫描包下所有class
		Set<Class<?>> classes = classReader.getClass(packageName, recursive);
		for (Class<?> clazz : classes) {
			// 注册带有Component和Service注解的类
			if (container.isRegister(clazz.getAnnotations())) {
				container.registBean(clazz);
			}
		}
		
		Set<String> beanNames = container.getBeanNames();
		for(String beanName : beanNames){
			LOGGER.debug("Load The Class：" + beanName);
		}
	}
	
}
