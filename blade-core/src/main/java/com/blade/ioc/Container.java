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
package com.blade.ioc;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * IOC container top interface
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public interface Container {

	/**
	 * According to the bean name and object function to obtain a bean object
	 * 
	 * @param name		bean name
	 * @param scope		object scope, single case or each time it is created
	 * @param <T> 		generic
	 * @return			return object
	 */
	<T> T getBean(String name, Scope scope);

    /**
     * According to the class and the object function to obtain a bean object
     * 
     * @param type		class type
     * @param scope		object scope, single case or each time it is created
     * @param <T> 		generic
     * @return			return object
     */
    <T> T getBean(Class<T> type, Scope scope);

    /**
     * @return Return the name of all bean
     */
    Set<String> getBeanNames();
    
    /**
     * @param <T> 		generic
     * @return Return a collection of all bean
     */
    <T> Collection<T> getBeans();
    
    /**
     * Class bean set in the IOC container for matching based on annotations
     * 
     * @param annotation	annotation class type
     * @return				return all class by annotation
     */
    List<Class<?>> getClassesByAnnotation(Class<? extends Annotation> annotation);
    
    /**
     * To obtain a set of bean objects that match the IOC container
     * 
     * @param annotation	annotation class type
     * @param <T> 			generic
     * @return				return all bean by annotation
     */
    <T> List<T> getBeansByAnnotation(Class<? extends Annotation> annotation);
    
    /**
     * To determine if there is a bean, according to the class type
     * 
     * @param clazz		class type
     * @return			return class is exist
     */
    boolean hasBean(Class<?> clazz);

    /**
     * According to name Bena to determine whether there is
     * 
     * @param name		bean name, usually class name
     * @return			return class is exist
     */
    boolean hasBean(String name);
    
    /**
     * Remove an bean object from the IOC container by name 
     * 
     * @param name			to remove the bean object name 
     * @return				return if remove success 
     */
    boolean removeBean(String name);
    
    /**
     * Remove an bean object from the IOC container by name
     * 
     * @param clazz			to remove the class bean type 
     * @return				return if remove success 
     */
    boolean removeBean(Class<?> clazz);
    
    /**
     * @return	clean container
     */
    boolean removeAll();
    
    /**
     * To determine whether the comment in the annotations can be registered into the IOC container
     * 
     * @param annotations	annotation array to detect 
     * @return				return is register
     */
    boolean isRegister(Annotation[] annotations);

    /**
     * Register bean with a name 
     * @param name		bean name
     * @param value		bean object
     * @return			return register bean
     */
    Object registerBean(String name, Object value);
    
    /**
     * Register an object to the bean container
     * 
     * @param object	bean object
     * @return			return register bean
     */
    Object registerBean(Object object);
    
    /**
     * Initialization IOC injection 
     */
    void initWired();
    
    /**
     * Inject a object 
     * @param object	to inject object 
     */
    void injection(Object object);
    
    /**
     * @return Returns the all bean objects<K,V> in the IOC container
     */
    Map<String, Object> getBeanMap();
    
}