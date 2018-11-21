package com.blade.ioc;

import com.blade.ioc.annotation.Bean;
import com.blade.ioc.bean.BeanDefine;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * The default IOC container implementation
 *
 * @author <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.5
 */
@Slf4j
public class SimpleIoc implements Ioc {

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
    @Override
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
    @Override
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
     * Register @Bean marked objects
     */
    @Override
    public <T> T addBean(Class<T> type) {
        Bean    beanAnnotation = type.getAnnotation(Bean.class);
        boolean isPrototype    = null == beanAnnotation || beanAnnotation.prototype();
        Object  bean           = addBean(type, isPrototype);
        return type.cast(bean);
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
        Set<String>  beanNames = this.getBeanNames();
        List<Object> beans     = new ArrayList<>(beanNames.size());
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

    /**
     * Add user-defined objects
     */
    private void addBean(String name, BeanDefine beanDefine) {
        if (pool.put(name, beanDefine) != null) {
            log.warn("Duplicated Bean: {}", name);
        }
    }

    /**
     * Register @Bean marked objects
     */
    private Object addBean(Class<?> type, boolean isPrototype) {
        return addBean(type.getName(), type, isPrototype);
    }

    /**
     * Register @Bean marked objects
     */
    private Object addBean(String name, Class<?> beanClass, boolean isPrototype) {
        BeanDefine beanDefine = this.getBeanDefine(beanClass, isPrototype);

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

        return Objects.requireNonNull(beanDefine).getBean();
    }

    private BeanDefine getBeanDefine(Class<?> beanClass, boolean isPrototype) {
        try {
            Object object = beanClass.newInstance();
            return new BeanDefine(object, beanClass, isPrototype);
        } catch (InstantiationException | IllegalAccessException e) {
            log.error("get BeanDefine error", e);
        }
        return null;
    }

}