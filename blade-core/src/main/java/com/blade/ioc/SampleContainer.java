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
import java.util.List;
import java.util.Map;
import java.util.Set;

import blade.kit.Assert;
import blade.kit.CloneKit;
import blade.kit.CollectionKit;
import blade.kit.StringKit;
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
    private Map<String, Object> beans = CollectionKit.newConcurrentHashMap();
    
    /**
     * All of the object storage and for class relations 
     */
    private Map<String, String> beanKeys = CollectionKit.newConcurrentHashMap();
    
    /**
     * Save all the notes class
     */
    private Map<Class<? extends Annotation>, List<Object>> annotaionBeans = CollectionKit.newConcurrentHashMap();
    
    
    public SampleContainer() {
    }
    
    public Map<String, Object> getBeanMap() {
        return beans;
    }
    
	@Override
    public <T> T getBean(String name, Scope scope) {
		Assert.notBlank(name);
		String className = beanKeys.get(name);
		if(StringKit.isBlank(className)){
			if(null == beans.get(name)){
				return null;
			} else {
				className = name;
			}
		}
		
    	Object obj = beans.get(className);
    	if(null != scope && null != Scope.SINGLE){
    		try {
				return (T) CloneKit.deepClone(obj);
			} catch (Exception e) {
				throw new IocException("clone object error", e);
			}
    	}
        return (T) obj;
    }

    @Override
    public <T> T getBean(Class<T> type, Scope scope) {
    	Assert.notNull(type);
    	return this.getBean(type.getName(), scope);
    }

    @Override
    public Set<String> getBeanNames() {
        return beanKeys.keySet();
    }
    
    @Override
    public Collection<Object> getBeans() {
        return beans.values();
    }

    @Override
    public boolean hasBean(Class<?> clazz) {
    	Assert.notNull(clazz);
    	String className = clazz.getName();
    	return beanKeys.containsValue(className);
    }

    @Override
    public boolean hasBean(String name) {
    	Assert.notBlank(name);
		return null != beanKeys.get(name);
    }
    
    @Override
	public boolean removeBean(String name) {
    	Assert.notBlank(name);
    	String className = beanKeys.get(name);
    	if(StringKit.isBlank(className)){
    		className = name;
    	} else {
    		beanKeys.remove(name);
		}
		beans.remove(className);
		return true;
	}

	@Override
	public boolean removeBean(Class<?> clazz) {
		Assert.notNull(clazz);
		return this.removeBean(clazz.getName());
	}

    @Override
	public Object registerBean(String name, Object value) {
    	Assert.notBlank(name);
    	Assert.notNull(value);
    	
    	Class<?> clazz = value.getClass();
		// Not abstract class, interface 
		if (isNormalClass(clazz)) {
			
			// If the container already exists, the name is directly returned 
			String className = beanKeys.get(name);
			if (StringKit.isNotBlank(className)) {
				return beans.get(className);
			}
			
			className = clazz.getName();
			beanKeys.put(name, className);
			if(null == beans.get(className)){
				beans.put(className, value);
			}
		    
		    // Achieve the interface corresponding storage 
		    Class<?>[] interfaces = clazz.getInterfaces();
		    if(interfaces.length > 0){
		    	for(Class<?> interfaceClazz : interfaces){
		    		String clsName = interfaceClazz.getName();
		    		this.registerParent(clsName, value);
		    	}
		    }
		    
		    // With annotation 
		    if(null != clazz.getDeclaredAnnotations()){
		    	putAnnotationMap(clazz, value);
		    }
		}
    	return value;
	}
    
	private void registerParent(String name, Object value) {
		
		Assert.notBlank(name);
		Assert.notNull(value);
		
    	Class<?> clazz = value.getClass();
			
		// If the container already exists, the name is directly returned 
		String className = beanKeys.get(name);
		if (StringKit.isNotBlank(className)) {
			return;
		}
		className = clazz.getName();
		beanKeys.put(name, className);
		if(null == beans.get(className)){
			beans.put(className, value);
		}
	}
    
    @Override
	public Object registerBean(Object object) {
    	
    	Assert.notNull(object);
    	
		String className = object.getClass().getName();
		return registerBean(className, object);
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
    	
    	Set<String> keys = beans.keySet();
    	for(String className : keys){
    		Object object = beans.get(className);
			injection(object);
    	}
    }
    
    // Assemble
    private Object recursiveAssembly(Class<?> clazz){
    	Object field = null;
    	if(null != clazz){
    		String className = beanKeys.get(clazz.getName());
    		field = beans.get(className);
    	}
    	return field;
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
		beanKeys.clear();
		beans.clear();
		annotaionBeans.clear();
		return true;
	}

	@Override
	public void injection(Object object) {
		// Traverse all fields 
	    try {
			Field[] fields = object.getClass().getDeclaredFields();
			for (Field field : fields) {
				// Need to inject the field 
			    Inject inject = field.getAnnotation(Inject.class);
			    if (null != inject ) {
			    	// Bean to be injected 
			        Object injectField = null;
			        String name = inject.name();
			        if(!name.equals("")){
	        			String className = beanKeys.get(name);
	        			if(null != className && !className.equals("")){
	        				injectField = beans.get(className);
	        			}
	        			if (null == injectField) {
				            throw new RuntimeException("Unable to load " + name);
				        }
	        		} else {
	        			if(inject.value() == Class.class){
	        				injectField = recursiveAssembly(field.getType());
				        } else {
				        	// Specify an assembly
				        	injectField = this.getBean(inject.value(), null);
				            if (null == injectField) {
				            	injectField = recursiveAssembly(inject.value());
				            }
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