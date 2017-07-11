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

    void addBean(Object bean);

    void addBean(String name, Object bean);

    <T> T addBean(Class<T> type);

    void setBean(Class<?> type, Object proxyBean);

    Object getBean(String name);

    <T> T getBean(Class<T> type);

    List<BeanDefine> getBeanDefines();

    BeanDefine getBeanDefine(Class<?> type);

    List<Object> getBeans();

    Set<String> getBeanNames();

    void remove(Class<?> type);

    void remove(String beanName);

    void clearAll();

}