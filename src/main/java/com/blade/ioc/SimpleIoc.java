package com.blade.ioc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * The default IOC container implementation
 *
 * @author <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.5
 */
public class SimpleIoc implements Ioc {

    private static final Logger log = LoggerFactory.getLogger(Ioc.class);

    private final Map<String, BeanDefine> pool = new HashMap<>(32);

    /**
     * Add user-defined objects
     */
    @Override
    public void addBean(Object bean) {
        addBean(bean.getClass().getName(), bean);
    }

    /**
     * Add user-defined objects
     */
    public void addBean(Class<?> beanClass, Object bean) {
        addBean(beanClass.getName(), bean);
    }

    /**
     * Add user-defined objects
     */
    public void addBean(String name, Object bean) {
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
        if (pool.put(name, beanDefine) != null) {
            log.warn("Duplicated Bean: {}", name);
        }

    }

    /**
     * Register @Bean marked objects
     */
    @Override
    public <T> T addBean(Class<T> type) {
        Object bean = addBean(type, true);
        return type.cast(bean);
    }

    /**
     * Register @Bean marked objects
     */
    public Object addBean(Class<?> type, boolean singleton) {
        return addBean(type.getName(), type, singleton);
    }

    /**
     * Register @Bean marked objects
     */
    public Object addBean(String name, Class<?> beanClass, boolean singleton) {
        BeanDefine beanDefine = this.getBeanDefine(beanClass, singleton);

        if (pool.put(name, beanDefine) != null) {
            log.warn("Duplicated Bean: {}", name);
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
        return beanDefine.getBean();
    }

    @Override
    public List<BeanDefine> getBeanDefines() {
        return new ArrayList<>(pool.values());
    }

    @Override
    public BeanDefine getBeanDefine(Class<?> type) {
        return this.getBeanDefine(type, true);
    }

    @Override
    public List<Object> getBeans() {
        Set<String> beanNames = this.getBeanNames();
        List<Object> beans = new ArrayList<>(beanNames.size());
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
    public void remove(String beanName) {
        pool.remove(beanName);
    }

    @Override
    public void remove(Class<?> type) {
        pool.remove(type.getSimpleName());
    }

    @Override
    public void clearAll() {
        pool.clear();
    }

}