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
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import blade.kit.Assert;
import blade.kit.CloneKit;
import blade.kit.CollectionKit;
import blade.kit.log.Logger;

import com.blade.annotation.Component;
import com.blade.annotation.Inject;
import com.blade.annotation.Path;

/**
 * IOC default implement
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
@SuppressWarnings("unchecked")
public class SampleContainer implements Container {
	
    private static final Logger LOGGER = Logger.getLogger(SampleContainer.class);

    /**
     * Save all bean objects, e.g: com.xxxx.User @Userx7asc
     */
    private Map<Class<?>, Object> beans = CollectionKit.newConcurrentHashMap();
    
    /**
     * Save all the notes class
     */
    private Map<Class<? extends Annotation>, List<Object>> annotaionBeans = CollectionKit.newConcurrentHashMap();
    
    
    public SampleContainer() {
    }
    
    public Map<Class<?>, Object> getBeanMap() {
        return beans;
    }
    
	@Override
    public <T> T getBean(String name, Scope scope) {
		Assert.notBlank(name);
		try {
			Class<T> clazz = (Class<T>) Class.forName(name);
			return getBean(clazz, scope);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
    }

    @Override
    public <T> T getBean(Class<T> type, Scope scope) {
    	Assert.notNull(type);
    	
    	Object obj = beans.get(type);
    	if(null != scope && null != Scope.SINGLE){
    		try {
				return (T) CloneKit.deepClone(obj);
			} catch (Exception e) {
				throw new IocException("clone object error", e);
			}
    	}
    	return type.cast(obj);
    }

    @Override
    public Set<String> getBeanNames() {
    	Set<Class<?>> classes = beans.keySet();
    	if(null != classes){
    		Set<String> beanNames = new HashSet<String>(classes.size());
    		for(Class<?> clazz : classes){
    			beanNames.add(clazz.getName());
        	}
    		return beanNames;
    	}
        return new HashSet<String>(0);
    }
    
    @Override
    public Collection<Object> getBeans() {
        return beans.values();
    }

    @Override
    public boolean hasBean(Class<?> clazz) {
    	if(null != clazz){
    		return beans.get(clazz) != null;
    	}
    	return false;
    }

    @Override
    public boolean hasBean(String name) {
		try {
			Class<?> clazz = Class.forName(name);
			return hasBean(clazz);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return false;
    }
    
	@Override
	public boolean removeBean(Class<?> clazz) {
		if(null != clazz && beans.containsKey(clazz)){
			beans.remove(clazz);
			return true;
		}
		return false;
	}
	
	private void registerParent(Class<?> clazz, Object value) {
		if(!beans.containsKey(clazz)){
			beans.put(clazz, value);
		}
	}
    
    @Override
	public Object registerBean(Object value) {
    	if(null != value){
    		Class<?> clazz = value.getClass();
    		// Not abstract class, interface 
    		if (isNormalClass(clazz)) {
    			
    			// If the container already exists, the name is directly returned 
    			if(beans.containsKey(clazz)){
    				return beans.get(clazz);
    			}
    			
    			beans.put(clazz, value);
    		    
    		    // Achieve the interface corresponding storage 
    		    Class<?>[] interfaces = clazz.getInterfaces();
    		    if(interfaces.length > 0){
    		    	for(Class<?> interfaceClazz : interfaces){
    		    		this.registerParent(interfaceClazz, value);
    		    	}
    		    }
    		    
    		    // With annotation 
    		    if(null != clazz.getDeclaredAnnotations()){
    		    	putAnnotationMap(clazz, value);
    		    }
    		    return value;
    		}
    	}
		return null;
	}
    
    /**
     * Add elements to annotationMap 
     * 
     * @param clazz		class type to be injected 
     * @param object	registered bean object 
     */
    private void putAnnotationMap(Class<?> clazz, Object object){
    	
    	Annotation[] annotations = clazz.getAnnotations();
    	List<Object> listObject = null;
    	for(Annotation annotation : annotations){
    		if(null != annotation){
    			listObject = annotaionBeans.get(annotation.annotationType());
    			if(CollectionKit.isEmpty(listObject)){
    				listObject = CollectionKit.newArrayList();
    			}
    			listObject.add(object);
    			this.put(annotation.annotationType(), listObject);
    		}
    	}
    }
    
    /**
     * AnnotationBean container storage 
     * 
     * @param clazz			allows the Annotation type to be registered 
     * @param listObject	list of objects to be injected 
     */
    private void put(Class<? extends Annotation> clazz, List<Object> listObject){
    	if(null == annotaionBeans.get(clazz)){
    		annotaionBeans.put(clazz, listObject);
    	}
    }
    
    /**
     * Initialization injection
     */
    @Override
    public void initWired() throws RuntimeException {
    	
    	Set<Class<?>> keys = beans.keySet();
    	for(Class<?> clazz : keys){
    		Object object = beans.get(clazz);
			injection(clazz, object);
    	}
    }
    
    // Assemble
    private Object recursiveAssembly(Class<?> clazz){
    	if(null != clazz && beans.containsKey(clazz)){
    		return beans.get(clazz);
    	}
    	return null;
    }
    
    
    /**
     * To determine whether the bean can be registered 
     * 
     * @param annotations		annotation class type arrays
     * @return 					return is register
     */
    @Override
    public boolean isRegister(Annotation[] annotations) {
        if (null == annotations || annotations.length == 0) {
            return false;
        }
        for (Annotation annotation : annotations) {
            if (annotation instanceof Component || annotation instanceof Path) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Is not interface and abstract class Class 
     * @param clazz 	class type
     * @return			return is interface && abstract class
     */
    private boolean isNormalClass(Class<?> clazz){
    	if(!Modifier.isAbstract(clazz.getModifiers()) && !clazz.isInterface()){
    		return true;
    	}
    	return false;
    }
    
	@Override
	public List<Class<?>> getClassesByAnnotation(Class<? extends Annotation> annotation) {
		List<Object> objectList = getBeansByAnnotation(annotation);
		if(!CollectionKit.isEmpty(objectList)){
			List<Class<?>> classList = CollectionKit.newArrayList(objectList.size());
			for(Object object : objectList){
				classList.add(object.getClass());
			}
			return classList;
		}
		return null;
	}

	@Override
	public <T> List<T> getBeansByAnnotation(Class<? extends Annotation> annotation) {
		return (List<T>) annotaionBeans.get(annotation);
	}
	
	@Override
	public boolean removeAll() {
		beans.clear();
		annotaionBeans.clear();
		return true;
	}

	@Override
	public void injection(Class<?> clazz, Object object) {
		
		if(null == clazz || null == object){
			return;
		}
		
		// Traverse all fields 
	    try {
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				// Need to inject the field 
			    Inject inject = field.getAnnotation(Inject.class);
			    if (null != inject ) {
			    	// Bean to be injected 
			        Object injectField = null;
        			if(inject.value() == Class.class){
        				injectField = recursiveAssembly(field.getType());
			        } else {
			        	// Specify an assembly
			        	injectField = this.getBean(inject.value(), null);
			            if (null == injectField) {
			            	injectField = recursiveAssembly(inject.value());
			            }
					}
			        
			        if (null == injectField) {
			            throw new RuntimeException("Unable to load " + field.getType().getName() + "!");
			        }
			        
			        boolean accessible = field.isAccessible();
			        field.setAccessible(true);
			        field.set(object, injectField);
			        field.setAccessible(accessible);
			    }
			}
		} catch (SecurityException e) {
        	LOGGER.error(e);
        } catch (IllegalArgumentException e) {
        	LOGGER.error(e);
        } catch (IllegalAccessException e) {
        	LOGGER.error(e);
        }
	}
	
}