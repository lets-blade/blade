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
import java.util.Map;
import java.util.Set;

/**
 * IOC容器顶层接口
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public interface Container {

	/**
	 * 根据bean名称和对象作用于获取一个bean对象
	 * 
	 * @param name		bean名称，可以是类名
	 * @param scope		对象作用域，单例或者每次都创建
	 * @return			一个bean对象
	 */
    Object getBean(String name, Scope scope);

    /**
     * 根据class和对象作用于获取一个bean对象
     * 
     * @param type		class类型
     * @param scope		对象作用域，单例或者每次都创建
     * @return			一个bean对象
     */
    Object getBean(Class<?> type, Scope scope);

    /**
     * @return 返回所有bean的名称集合
     */
    Set<String> getBeanNames();
    
    /**
     * @return 返回所有bean的集合
     */
    Collection<Object> getBeans();
    
    /**
     * 根据注解获取ioc容器中匹配的bean class集合
     * 
     * @param annotation	annotation class类型
     * @return				返回符合annotation class类型的所有class
     */
    List<Class<?>> getClassesByAnnotation(Class<? extends Annotation> annotation);
    
    /**
     * 根据注解获取ioc容器中匹配的bean对象集合
     * 
     * @param annotation	annotation class类型
     * @return				返回符合annotation class类型的所有bean
     */
    List<Object> getBeansByAnnotation(Class<? extends Annotation> annotation);
    
    /**
     * 判断是否存在一个bean，根据class类型
     * 
     * @param clazz			类的class类型
     * @return				true：存在，false：不存在
     */
    boolean hasBean(Class<?> clazz);

    /**
     * 判断是否存在一个bean，根据bena name
     * 
     * @param name			bean的名称，一般是class名称
     * @return				true：存在，false：不存在
     */
    boolean hasBean(String name);
    
    /**
     * 根据名称从ioc容器中移除一个bean对象
     * 
     * @param name			要移除的bean对象名称
     * @return				true：成功，false：失败
     */
    boolean removeBean(String name);
    
    /**
     * 根据名称从ioc容器中移除一个bean对象
     * 
     * @param clazz			要移除的bean class类型
     * @return				true：成功，false：失败
     */
    boolean removeBean(Class<?> clazz);
    
    /**
     * @return	清空容器
     */
    boolean removeAll();
    
    /**
     * 获取annotations中的注解是否可以注册进入ioc容器
     * 
     * @param annotations	annotations要检测的annotation数组
     * @return				true：可以注册，false：不可以注册
     */
    boolean isRegister(Annotation[] annotations);

    /**
     * 注册一个class类型的bean到容器中
     * 
     * @param clazz			要注册的class类型
     * @return				返回注册后的bean对象
     */
    Object registBean(Class<?> clazz);
    
    /**
     * 注册一个对象到bean容器中
     * 
     * @param object		要注册的object
     * @return				返回注册后的Bean实例
     */
    Object registBean(Object object);
    
    /**
     * 注册一个class集合进入ioc容器
     * 
     * @param classes		要注册的class集合
     */
    void registBean(Set<Class<?>> classes);

    /**
     * 初始化IOC
     */
    void initWired();

    /**
     * @return 返回ioc容器中的所有bean对象的K,V
     */
    Map<String, Object> getBeanMap();
    
}