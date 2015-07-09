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
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import blade.annotation.Component;
import blade.annotation.Inject;
import blade.annotation.Path;
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
public class DefaultContainer implements Container {
	
    private static final Logger LOGGER = Logger.getLogger(DefaultContainer.class);

    /**
     * 保存所有bean对象
     */
    private static final Map<String, Object> beansMap = CollectionKit.newConcurrentHashMap();
    
    /**
     * 保存所有注解的class
     */
    private static final Map<Class<? extends Annotation>, List<Object>> annotationMap = CollectionKit.newConcurrentHashMap();
    
    private DefaultContainer() {
    }
    
    public static DefaultContainer single() {
        return DefaultContainerHoder.single;
    }
    
    private static class DefaultContainerHoder {
        private static final DefaultContainer single = new DefaultContainer();
    }

    public Map<String, Object> getBeanMap() {
        return beansMap;
    }
    
    @Override
    public Object getBean(String name, Scope scope) {
    	Object obj = beansMap.get(name);
    	if(null != scope && scope == Scope.PROTOTYPE){
    		try {
				return CloneKit.deepClone(obj);
			} catch (Exception e) {
				LOGGER.error("克隆对象失败," + e.getMessage());
			}
    	}
        return obj;
    }

    @Override
    public Object getBean(Class<?> type, Scope scope) {
        Iterator<Object> it = beansMap.values().iterator();
        while (it.hasNext()) {
            Object obj = it.next();
            if (type.isAssignableFrom(obj.getClass())) {
            	if(null != scope && scope == Scope.PROTOTYPE){
            		try {
						return CloneKit.deepClone(obj);
					} catch (Exception e) {
						LOGGER.error("克隆对象失败," + e.getMessage());
					}
            	} else {
            		return obj;
				}
            }
        }
        return null;
    }

    @Override
    public Set<String> getBeanNames() {
        return beansMap.keySet();
    }
    
    @Override
    public Collection<Object> getBeans() {
        return beansMap.values();
    }

    @Override
    public boolean hasBean(Class<?> clz) {
        if (null != this.getBean(clz, Scope.SINGLE)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean hasBean(String name) {
        if (null != this.getBean(name, Scope.SINGLE)) {
            return true;
        }
        return false;
    }
    
    @Override
	public boolean removeBean(String name) {
    	Object object = beansMap.remove(name);
		return (null != object);
	}

	@Override
	public boolean removeBean(Class<?> clazz) {
		Object object = beansMap.remove(clazz.getName());
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
    	if(null == beansMap.get(name)){
    		beansMap.put(name, object);
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
    	for(Annotation annotation : annotations){
    		if(null != annotation){
    			List<Object> listObject = annotationMap.get(annotation.annotationType());
    			if(CollectionKit.isEmpty(listObject)){
    				listObject = CollectionKit.newArrayList();
    			}
    			listObject.add(object);
    			put(annotation.annotationType(), listObject);
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
    	if(null == annotationMap.get(clazz)){
    		annotationMap.put(clazz, listObject);
    	}
    }
    
    /**
     * 初始化注入
     */
    @Override
    public void initWired() {
        Iterator<Object> it = beansMap.values().iterator();
        try {
            while (it.hasNext()) {
            	
                Object obj = it.next();
                
                // 所有字段
                Field[] fields = obj.getClass().getDeclaredFields();
                for (Field field : fields) {
                	
                	// 需要注入的字段
                    Inject inject = field.getAnnotation(Inject.class);
                    if (null != inject) {
                    	
                        // 要注入的字段
                        Object injectField = this.getBean(field.getType(), Scope.SINGLE);
                        
                        // 指定装配的类
                        if (inject.value() != Class.class) {
                        	injectField = this.getBean(inject.value(), Scope.SINGLE);
                            // 容器有该类
                            if (null == injectField) {
                            	injectField = this.registBean(inject.value());
                            }
                        } else{
                        	// 没有指定装配class, 容器没有该类，则创建一个对象放入容器
                            if (null == injectField) {
                            	injectField = this.registBean(field.getType());
                            }
                        }
                        if (null == injectField) {
                            throw new RuntimeException("Unable to load " + field.getType().getCanonicalName() + "！");
                        }
                        boolean accessible = field.isAccessible();
                        field.setAccessible(true);
                        field.set(obj, injectField);
                        field.setAccessible(accessible);
                    }
                }
            }
        } catch (SecurityException e) {
        	LOGGER.error(e.getMessage());
        } catch (IllegalArgumentException e) {
        	LOGGER.error(e.getMessage());
        } catch (IllegalAccessException e) {
        	LOGGER.error(e.getMessage());
        }
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
	public List<Object> getBeansByAnnotation(Class<? extends Annotation> annotation) {
		return annotationMap.get(annotation);
	}
	
	@Override
	public void registBean(Set<Class<?>> classes) {
		if(!CollectionKit.isEmpty(classes)){
			for(Class<?> clazz : classes){
				this.registBean(clazz);
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
		beansMap.clear();
		annotationMap.clear();
		return true;
	}

}