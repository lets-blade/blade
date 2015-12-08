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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import blade.exception.BladeException;
import blade.kit.CloneKit;
import blade.kit.CollectionKit;
import blade.kit.StringKit;
import blade.kit.log.Logger;

import com.blade.Aop;
import com.blade.annotation.Component;
import com.blade.annotation.Inject;
import com.blade.annotation.Path;

/**
 * 默认的IOC容器实现
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
@SuppressWarnings("unchecked")
public class SampleContainer implements Container {
	
    private static final Logger LOGGER = Logger.getLogger(SampleContainer.class);

    /**
     * 保存所有bean对象 如：com.xxxx.User @Userx7asc
     */
    private Map<String, Object> beans = CollectionKit.newConcurrentHashMap();
    
    /**
     * 存储所有的对象名和对于的类名关系
     */
    private Map<String, String> beanKeys = CollectionKit.newConcurrentHashMap();
    
    /**
     * 保存所有注解的class
     */
    private Map<Class<? extends Annotation>, List<Object>> annotaionBeans = CollectionKit.newConcurrentHashMap();
    
    
    public SampleContainer() {
    }
    
    public Map<String, Object> getBeanMap() {
        return beans;
    }
    
	@Override
    public <T> T getBean(String name, Scope scope) {
		
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
				LOGGER.error("克隆对象失败," + e.getMessage());
			}
    	}
        return (T) obj;
    }

    @Override
    public <T> T getBean(Class<T> type, Scope scope) {
    	return this.getBean(type.getCanonicalName(), scope);
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
    	String className = clazz.getCanonicalName();
    	return beanKeys.containsValue(className);
    }

    @Override
    public boolean hasBean(String name) {
		return null != beanKeys.get(name);
    }
    
    @Override
	public boolean removeBean(String name) {
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
		return this.removeBean(clazz.getCanonicalName());
	}

    /**
     * 注册一个bean对象到容器里
     * 
     * @param clazz 要注册的class
     * @return		返回注册后的bean对象
     */
    @Override
    public Object registerBean(Class<?> clazz) {
    	
        String name = clazz.getCanonicalName();
        Object object = null;
        
		//非抽象类、接口
		if (isNormalClass(clazz)) {
			object = Aop.create(clazz);
			return registerBean(name, object);
		}
		return object;
	}
    
    @Override
	public Object registerBean(String name, Object value) {
    	Class<?> clazz = value.getClass();
		//非抽象类、接口
		if (isNormalClass(clazz)) {
			
			// 如果容器已经存在该名称对于的对象，直接返回
			String className = beanKeys.get(name);
			if (StringKit.isNotBlank(className)) {
				return beans.get(className);
			}
			
			className = clazz.getCanonicalName();
			beanKeys.put(name, className);
			if(null == beans.get(className)){
				beans.put(className, value);
			}
		    
		    //实现的接口对应存储
		    Class<?>[] interfaces = clazz.getInterfaces();
		    if(interfaces.length > 0){
		    	for(Class<?> interfaceClazz : interfaces){
		    		this.registerBean(interfaceClazz.getCanonicalName(), value);
		    	}
		    }
		    
		    //带有annotation
		    if(null != clazz.getDeclaredAnnotations()){
		    	putAnnotationMap(clazz, value);
		    }
		}
    	return value;
	}
    
    @Override
	public Object registerBean(Object object) {
		String className = object.getClass().getCanonicalName();
		return registerBean(className, object);
	}
    
    /**
     * 给annotationMap添加元素
     * 
     * @param clazz			要注入的class类型
     * @param object		注册的bean对象
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
     * annotationBean容器存储
     * 
     * @param clazz			允许注册的Annotation类型
     * @param listObject	要注入的对象列表
     */
    private void put(Class<? extends Annotation> clazz, List<Object> listObject){
    	if(null == annotaionBeans.get(clazz)){
    		annotaionBeans.put(clazz, listObject);
    	}
    }
    
    /**
     * 初始化注入
     */
    @Override
    public void initWired() throws RuntimeException {
        Iterator<Entry<String, Object>> it = beans.entrySet().iterator();
        while (it.hasNext()) {
			Map.Entry<String, Object> entry = (Map.Entry<String, Object>) it.next();
			Object object = entry.getValue();
			injection(object);
		}
    }
    
    // 装配
    private Object recursiveAssembly(Class<?> clazz){
    	Object field = null;
    	if(null != clazz){
    		if(!clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers())){
    			field = this.registerBean(clazz);
    		}
    	}
    	return field;
    }
    
    
    /**
     * 判断是否是可以注册的bean
     * 
     * @param annotations		注解类型
     * @return 					true:可以注册 false:不可以注册
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
     * 是否是一个非接口和抽象类的Class
     * @param clazz
     * @return
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
	public void registerBean(Set<Class<?>> classes) {
		if(!CollectionKit.isEmpty(classes)){
			for(Class<?> clazz : classes){
				this.registerBean(clazz);
			}
		}
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
		// 所有字段
	    try {
			Field[] fields = object.getClass().getDeclaredFields();
			for (Field field : fields) {
				// 需要注入的字段
			    Inject inject = field.getAnnotation(Inject.class);
			    if (null != inject ) {
			    	
			    	// 要注入的字段
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
				        	// 指定装配的类
				        	injectField = this.getBean(inject.value(), null);
				            if (null == injectField) {
				            	injectField = recursiveAssembly(inject.value());
				            }
						}
					}
			        
			        if (null == injectField) {
			            throw new BladeException("Unable to load " + field.getType().getCanonicalName() + "！");
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