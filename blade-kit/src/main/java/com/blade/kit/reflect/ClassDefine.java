package com.blade.kit.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public final class ClassDefine {

	private static final ConcurrentHashMap<Class<?>, ClassDefine> pool = new ConcurrentHashMap<Class<?>, ClassDefine>(128);

	private final Class<?> clazz;

    private ClassDefine(Class<?> type) {
        this.clazz = type;
    }
    
	public static ClassDefine create(Class<?> clazz){
		ClassDefine classDefine = pool.get(clazz);
        if (classDefine == null) {
        	classDefine = new ClassDefine(clazz);
        	ClassDefine old = pool.putIfAbsent(clazz, classDefine);
            if (old != null) {
            	classDefine = old;
            }
        }
        return classDefine;
	}
	
	@SuppressWarnings("unchecked")
    public <T> Class<T> getType() {
        return (Class<T>) clazz;
    }

    public String getName() {
        return clazz.getName();
    }

    public String getSimpleName() {
        return clazz.getSimpleName();
    }

    public ClassDefine getSuperKlass() {
        Class<?> superKlass = clazz.getSuperclass();
        return (superKlass == null) ? null : ClassDefine.create(superKlass);
    }

    public List<ClassDefine> getInterfaces() {
        Class<?>[] interfaces = clazz.getInterfaces();
        if (interfaces.length == 0) {
            return Collections.emptyList();
        }
        List<ClassDefine> results = new ArrayList<ClassDefine>(interfaces.length);
        for (Class<?> intf : interfaces) {
            results.add(ClassDefine.create(intf));
        }
        return results;
    }

    // ------------------------------------------------------------------
    public Annotation[] getAnnotations() {
        return clazz.getAnnotations();
    }

    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return clazz.getAnnotation(annotationClass);
    }

    public <T extends Annotation> boolean isAnnotationPresent(Class<T> annotationClass) {
        return clazz.isAnnotationPresent(annotationClass);
    }
    
    public Field[] getDeclaredFields() {
        return clazz.getDeclaredFields();
    }

    // ------------------------------------------------------------------
    public int getModifiers() {
        return clazz.getModifiers();
    }

    public boolean isInterface() {
        return Modifier.isInterface(getModifiers());
    }

    public boolean isAbstract() {
        return Modifier.isAbstract(getModifiers());
    }

    public boolean isStatic() {
        return Modifier.isStatic(getModifiers());
    }

    public boolean isPrivate() {
        return Modifier.isPrivate(getModifiers());
    }

    public boolean isProtected() {
        return Modifier.isProtected(getModifiers());
    }

    public boolean isPublic() {
        return Modifier.isPublic(getModifiers());
    }
}
