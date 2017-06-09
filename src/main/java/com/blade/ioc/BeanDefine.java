package com.blade.ioc;

/**
 * Bean Define, IOC to define a target
 *
 * @author <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.5
 */
public class BeanDefine {

    private Object bean;
    private Class<?> type;
    private boolean isSignle;

    public BeanDefine(Object bean) {
        this(bean, bean.getClass());
    }

    public BeanDefine(Object bean, Class<?> type) {
        this.bean = bean;
        this.type = type;
        this.isSignle = true;
    }

    public BeanDefine(Object bean, Class<?> type, boolean isSingle) {
        this.bean = bean;
        this.type = type;
        this.isSignle = isSingle;
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

    public boolean isSignle() {
        return isSignle;
    }

    public void setSignle(boolean isSignle) {
        this.isSignle = isSignle;
    }

}