/**
 * Copyright (c) 2016, biezhi 王爵 (biezhi.me@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blade.ioc;

import com.blade.ioc.loader.IocLoader;
import com.blade.kit.Assert;
import com.blade.kit.IocKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;
import java.util.*;

/**
 * The default IOC container implementation
 *
 * @author    <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.5
 */
public class SimpleIoc implements Ioc {

	private static final Logger LOGGER = LoggerFactory.getLogger(Ioc.class);

	private final Map<String, BeanDefine> pool = new HashMap<>(32);

	/**
	 * ioc loader
	 */
	@Override
	public void load(IocLoader loader) {
		loader.load(this);
    }

	/**
	 * Add user-defined objects
	 */
	@Override
	public void addBean(Object bean) {
		Assert.notNull(bean);
		addBean(bean.getClass().getName(), bean);
    }

	/**
	 * Add user-defined objects
	 */
	public void addBean(Class<?> beanClass, Object bean) {
		Assert.notNull(beanClass);
		addBean(beanClass.getName(), bean);
    }

    /**
	 * Add user-defined objects
	 */
	public void addBean(String name, Object bean) {
		Assert.notNull(bean);
		BeanDefine beanDefine = new BeanDefine(bean);
		addBean(name, beanDefine);

        // add interface
        Class<?>[] interfaces = beanDefine.getType().getInterfaces();
		if (interfaces.length > 0) {
			for (Class<?> interfaceClazz : interfaces) {
				this.addBean(interfaceClazz.getName(), beanDefine);
			}
		}
	}

    /**
     * Update BeanDefine
     */
    public void setBean(Class<?> type, Object proxyBean) {
		Assert.notNull(proxyBean);

		BeanDefine beanDefine = pool.get(type.getName());
		if (beanDefine != null) {
			beanDefine.setBean(proxyBean);
		} else {
			beanDefine = new BeanDefine(proxyBean, type);
		}
		pool.put(type.getName(), beanDefine);
	}

    /**
	 * Add user-defined objects
	 */
	public void addBean(String name, BeanDefine beanDefine) {

		Assert.notNull(name);
		Assert.notNull(beanDefine);

//    	LOGGER.debug("addBean: {}", name);

        if (pool.put(name, beanDefine) != null) {
			LOGGER.warn("Duplicated Bean: {}", name);
		}

    }

    /**
     * Register @Component marked objects
     */
    @Override
    public Object addBean(Class<?> type) {
        return addBean(type, true);
    }

    /**
     * Register @Component marked objects
     */
    public Object addBean(Class<?> type, boolean singleton) {
		Assert.notNull(type);
		return addBean(type.getName(), type, singleton);
    }

    /**
     * Register @Component marked objects
     */
    public Object addBean(String name, Class<?> beanClass, boolean singleton) {

		Assert.notNull(name);
		Assert.notNull(beanClass);
		Assert.isFalse(beanClass.isInterface(), "Must not be interface: %s", beanClass.getName());
		Assert.isFalse(Modifier.isAbstract(beanClass.getModifiers()), "Must not be abstract class: %s", beanClass.getName());

//        LOGGER.debug("addBean: {} = {}", name, beanClass.getName());

        BeanDefine beanDefine = this.getBeanDefine(beanClass, singleton);

        if (pool.put(name, beanDefine) != null) {
			LOGGER.warn("Duplicated Bean: {}", name);
		}

		// add interface
        Class<?>[] interfaces = beanClass.getInterfaces();
		if (interfaces.length > 0) {
			for (Class<?> interfaceClazz : interfaces) {
				if (null != this.getBean(interfaceClazz)) {
					break;
				}
				this.addBean(interfaceClazz.getName(), beanDefine);
			}
		}

		return beanDefine.getBean();
	}

	public BeanDefine getBeanDefine(Class<?> beanClass, boolean singleton) {
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
		Object bean = this.getBean(type.getName());
		try {
			return type.cast(bean);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Object getBean(String name) {
		BeanDefine beanDefine = pool.get(name);
		if (beanDefine == null) {
            return null;
        }
        return IocKit.getBean(beanDefine);
	}

	@Override
	public List<BeanDefine> getBeanDefines() {
		return new ArrayList<BeanDefine>(pool.values());
	}

	@Override
	public List<Object> getBeans() {
		Set<String> beanNames = this.getBeanNames();
		List<Object> beans = new ArrayList<Object>(beanNames.size());
		for (String beanName : beanNames) {
			Object bean = this.getBean(beanName);
			if (null != bean) {
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