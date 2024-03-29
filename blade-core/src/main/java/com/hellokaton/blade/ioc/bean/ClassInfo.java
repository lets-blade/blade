package com.hellokaton.blade.ioc.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * ClassInfo
 *
 * @author <a href="mailto:hellokaton@gmail.com" target="_blank">hellokaton</a>
 * @since 1.0
 */
@Data
@EqualsAndHashCode
public class ClassInfo {

    private String className;
    private Class<?> clazz;

    public ClassInfo(String className) {
        this.className = className;
    }

    public ClassInfo(Class<?> clazz) {
        this.clazz = clazz;
        this.className = clazz.getName();
    }

    public ClassInfo(String className, Class<?> clazz) {
        this.clazz = clazz;
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public Object newInstance() {
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return clazz.toString();
    }
}