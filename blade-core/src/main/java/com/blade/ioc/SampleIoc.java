/**
 * Copyright (c) 2016, biezhi 王爵 (biezhi.me@gmail.com)
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.blade.ioc.loader.IocLoader;

import blade.kit.log.Logger;

public class SampleIoc implements Ioc{
	
	private static final Logger LOGGER = Logger.getLogger(Ioc.class);
	
	private final Map<String, BeanDefine> pool = new HashMap<String, BeanDefine>();
	
	@Override
	public void load(IocLoader loader) {
        loader.load(this);
    }
	
	// 添加用户自定义的对象
	@Override
    public void addBean(Object beanObject) {
        addBean(beanObject.getClass().getName(), beanObject);
    }

    // 添加用户自定义的对象
    public void addBean(Class<?> beanClass, Object beanObject) {
        addBean(beanClass.getName(), beanObject);
    }

    // 添加用户自定义的对象
    public void addBean(String name, Object beanObject) {
        addBean(name, new BeanDefine(beanObject));
    }

    // 添加用户自定义的对象
    public void addBean(String name, BeanDefine beanDefine) {
    	
    	LOGGER.debug("addBean: %s", name);

        if (pool.put(name, beanDefine) != null) {
        	LOGGER.warn("Duplicated Bean: %s", name);
        }
    }

    // 注册 @Component 标注的对象
    @Override
    public void addBean(Class<?> type) {
        addBean(type, true);
    }

    // 注册 @Component 标注的对象
    public void addBean(Class<?> type, boolean singleton) {
        addBean(type.getName(), type, singleton);
    }
    
    // 注册 @Component 标注的对象
    public void addBean(String name, Class<?> beanClass, boolean singleton) {
    	
        LOGGER.debug("addBean: %s", name, beanClass.getName());
        
        BeanDefine beanDefine = this.getBeanDefine(beanClass, singleton);
        
        if (pool.put(name, beanDefine) != null) {
        	LOGGER.warn("Duplicated Bean: %s", name);
        }
        
        Class<?>[] interfaces = beanClass.getInterfaces();
	    if(interfaces.length > 0){
	    	for(Class<?> interfaceClazz : interfaces){
	    		this.addBean(interfaceClazz.getName(), beanDefine);
	    	}
	    }
	    
    }

    private BeanDefine getBeanDefine(Class<?> beanClass, boolean singleton) {
    	try {
			Object object = beanClass.newInstance();
			return new BeanDefine(object, beanClass, singleton);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
    	return null;
    }
    
    @Override
	public <T> T getBean(Class<T> type) {
		return type.cast(getBean(type.getName()));
	}

	@Override
	public Object getBean(String name) {
		BeanDefine beanDefine = pool.get(name);
        if (beanDefine == null) {
            return null;
        }
        return IocKit.getBean(this, beanDefine);
	}

	@Override
	public List<Object> getBeans() {
		Set<String> beanNames = this.getBeanNames();
		List<Object> beans = new ArrayList<Object>(beanNames.size());
		for(String beanName : beanNames){
			Object bean = this.getBean(beanName);
			if(null != bean){
				beans.add(bean);
			}
		}
		return beans;
	}

	@Override
	public Set<String> getBeanNames() {
		return pool.keySet();
	}

	@Override
	public void clearAll() {
		pool.clear();
	}
	
}
