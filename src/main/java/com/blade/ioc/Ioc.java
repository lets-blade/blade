package com.blade.ioc;

import java.util.List;
import java.util.Set;

/**
 * IOC container, it provides an interface for registration and bean.
 *
 * @author <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.5
 */
public interface Ioc {

    /**
     * Add bean to ioc container
     *
     * @param bean bean instance
     */
    void addBean(Object bean);

    /**
     * Add bean to ioc container
     *
     * @param name bean name
     * @param bean bean instance
     */
    void addBean(String name, Object bean);

    /**
     * Create bean by type, and register to ioc container
     *
     * @param type bean class type
     * @param <T>  class Type
     * @return return no instance constructor
     */
    <T> T addBean(Class<T> type);

    /**
     * Set bean, e.g aop proxy
     *
     * @param type      bean class type
     * @param proxyBean bean instance by proxy
     */
    void setBean(Class<?> type, Object proxyBean);

    /**
     * Get bean instance by name
     *
     * @param name bean name
     * @return return bean instance
     */
    Object getBean(String name);

    /**
     * Get bean instance by class type
     *
     * @param type class type
     * @param <T>  type
     * @return return bean instance
     */
    <T> T getBean(Class<T> type);

    /**
     * Get ioc container bean defines
     *
     * @return ioc container bean defines
     */
    List<BeanDefine> getBeanDefines();

    /**
     * Get BeanDefine by bean type
     *
     * @param type bean class type
     * @return return BeanDefine instance
     */
    BeanDefine getBeanDefine(Class<?> type);

    /**
     * Get ioc container all beans
     *
     * @return return bean list
     */
    List<Object> getBeans();

    /**
     * Get ioc bean names
     *
     * @return return bean name set
     */
    Set<String> getBeanNames();

    /**
     * Remove bean by class type
     *
     * @param type bean class type
     */
    void remove(Class<?> type);

    /**
     * Remove bean by name
     *
     * @param beanName bean name
     */
    void remove(String beanName);

    /**
     * Clean ioc container
     */
    void clearAll();

}