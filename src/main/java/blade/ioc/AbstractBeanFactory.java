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
package blade.ioc;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * 抽象bean工厂，用于注册和获取bean对象
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public abstract class AbstractBeanFactory {
	
	protected Container container = DefaultContainer.single();
	
	public abstract Object getBean(String className);
	
	public abstract Object getBean(Class<?> clazz);
	
	public boolean resetBean(Class<?> clazz, Object object){
		System.out.println("resetBean object=" + object);
		if(null != clazz.getInterfaces() && null != object){
			container.removeBean(clazz);
			container.getBeanMap().put(clazz.getName(), object);
		}
		return true;
	}
	
	public Set<String> getBeanNames(){
		return container.getBeanNames();
	}
	
	public Collection<Object> getBeans(){
		return container.getBeans();
	}
	
	public List<Object> getBeansByAnnotation(Class<? extends Annotation> annotation){
		return container.getBeansByAnnotation(annotation);
	}
	
	public List<Class<?>> getClassesByAnnotation(Class<? extends Annotation> annotation){
		return container.getClassesByAnnotation(annotation);
	}
	
}