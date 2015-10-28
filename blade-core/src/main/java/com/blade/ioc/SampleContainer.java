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

import com.blade.annotation.Component;
import com.blade.annotation.Inject;
import com.blade.annotation.Path;

import blade.exception.BladeException;
import blade.kit.CloneKit;
import blade.kit.CollectionKit;
import blade.kit.ReflectKit;
import blade.kit.log.Logger;

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
     * 保存所有bean对象
     */
    private static final Map<String, Object> BEAN_CONTAINER = CollectionKit.newConcurrentHashMap();
    
    /**
     * 保存所有注解的class
     */
    private static final Map<Class<? extends Annotation>, List<Object>> ANNOTATION_CONTAINER = CollectionKit.newConcurrentHashMap();
    
    private SampleContainer() {
    }
    
    public static SampleContainer single() {
        return DefaultContainerHoder.single;
    }
    
    private static class DefaultContainerHoder {
        private static final SampleContainer single = new SampleContainer();
    }

    public Map<String, Object> getBeanMap() {
        return BEAN_CONTAINER;
    }
    
	@Override
    public <T> T getBean(String name, Scope scope) {
    	Object obj = BEAN_CONTAINER.get(name);
    	if(null != scope && scope == Scope.PROTOTYPE){
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
        Set<String> keys = BEAN_CONTAINER.keySet();
        for(String key : keys){
        	Object obj = BEAN_CONTAINER.get(key);
            if (type.isAssignableFrom(obj.getClass())) {
            	if(null != scope && scope == Scope.PROTOTYPE){
            		try {
						return (T) CloneKit.deepClone(obj);
					} catch (Exception e) {
						LOGGER.error("克隆对象失败," + e.getMessage());
					}
            	} else {
            		return (T) obj;
				}
            }
        }
        return null;
    }

    @Override
    public Set<String> getBeanNames() {
        return BEAN_CONTAINER.keySet();
    }
    
    @Override
    public Collection<Object> getBeans() {
        return BEAN_CONTAINER.values();
    }

    @Override
    public boolean hasBean(Class<?> clz) {
        if (null != single().getBean(clz, Scope.SINGLE)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean hasBean(String name) {
        if (null != single().getBean(name, Scope.SINGLE)) {
            return true;
        }
        return false;
    }
    
    @Override
	public boolean removeBean(String name) {
    	Object object = BEAN_CONTAINER.remove(name);
		return (null != object);
	}

	@Override
	public boolean removeBean(Class<?> clazz) {
		Object object = BEAN_CONTAINER.remove(clazz.getName());
		return (null != object);
	}

    /**
     * 注册一个bean对象到容器里
     * 
     * @param clazz 要注册的class
     * @return		返回注册后的bean对象
     */
    @Override
    public Object registBean(Class<?> clazz) {
    	
        String name = clazz.getCanonicalName();
        
        Object object = null;
        
		//非抽象类、接口
		if (!Modifier.isAbstract(clazz.getModifiers()) && !clazz.isInterface()) {
			
		    object = ReflectKit.newInstance(clazz);
		    
		    put(name, object);
		    //实现的接口对应存储
		    if(clazz.getInterfaces().length > 0){
		    	put(clazz.getInterfaces()[0].getCanonicalName(), object);
		    }
		    
		    //带有annotation
		    if(null != clazz.getDeclaredAnnotations()){
		    	putAnnotationMap(clazz, object);
		    }
		}
		return object;
	}
    
    /**
     * bean容器存储
     * 
     * @param name			要进入IOC容器的bean名称
     * @param object		要进入IOC容器的bean对象
     */
    private void put(String name, Object object){
    	if(!BEAN_CONTAINER.containsValue(object)){
    		BEAN_CONTAINER.put(name, object);
    	}
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
    			listObject = ANNOTATION_CONTAINER.get(annotation.annotationType());
    			if(CollectionKit.isEmpty(listObject)){
    				listObject = CollectionKit.newArrayList();
    			}
    			listObject.add(object);
    			single().put(annotation.annotationType(), listObject);
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
    	if(null == ANNOTATION_CONTAINER.get(clazz)){
    		ANNOTATION_CONTAINER.put(clazz, listObject);
    	}
    }
    
    /**
     * 初始化注入
     */
    @Override
    public void initWired() throws RuntimeException {
        Iterator<Entry<String, Object>> it = BEAN_CONTAINER.entrySet().iterator();
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
    		// 是接口或者抽象类
    		if(clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())){
    			String implClassName = clazz.getPackage().getName() + ".impl." + clazz.getSimpleName() + "Impl";
    			return ReflectKit.newInstance(implClassName);
    		} else {
    			field = single().registBean(clazz);
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
		return (List<T>) ANNOTATION_CONTAINER.get(annotation);
	}
	
	@Override
	public void registBean(Set<Class<?>> classes) {
		if(!CollectionKit.isEmpty(classes)){
			for(Class<?> clazz : classes){
				single().registBean(clazz);
			}
		}
	}

	@Override
	public Object registBean(Object object) {
		String name = object.getClass().getName();
		put(name, object);
		return object;
	}

	@Override
	public boolean removeAll() {
		BEAN_CONTAINER.clear();
		ANNOTATION_CONTAINER.clear();
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
			        Object injectField = single().getBean(field.getType(), Scope.SINGLE);
			    	// 指定装配到哪个class
			    	if(inject.value() != Class.class){
			    		// 指定装配的类
			            injectField = single().getBean(inject.value(), Scope.SINGLE);
			            
			            if (null == injectField) {
			            	injectField = recursiveAssembly(inject.value());
			            }
			            
			    	} else {
			    		if (null == injectField) {
			            	injectField = recursiveAssembly(field.getType());
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