package com.blade.ioc.bean;

/**
 * Bean Define, IOC to define a target
 *
 * @author <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.5
 */
public class BeanDefine {

    private Object   bean;
    private Class<?> type;
    private boolean  isSingleton;
    private boolean  hasPrototypeField;

    public BeanDefine(Object bean) {
        this(bean, bean.getClass());
    }

    public BeanDefine(Object bean, Class<?> type) {
        this.bean = bean;
        this.type = type;
        this.isSingleton = true;
    }

    public BeanDefine(Object bean, Class<?> type, boolean isSingleton) {
        this.bean = bean;
        this.type = type;
        this.isSingleton = isSingleton;
    }

    public Object getBean() {
        return bean;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public boolean isSingleton() {
        return isSingleton;
    }

    public void setSingleton(boolean isSingleton) {
        this.isSingleton = isSingleton;
    }

    public boolean isHasPrototypeField() {
        return hasPrototypeField;
    }

    public void setHasPrototypeField(boolean hasPrototypeField) {
        this.hasPrototypeField = hasPrototypeField;
    }
}