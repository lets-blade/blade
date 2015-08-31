package org.sql2o.reflection;

import org.sql2o.Sql2oException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ReflectionObjectConstructorFactory implements ObjectConstructorFactory {
    public ObjectConstructor newConstructor(final Class<?> clazz) {
        try {
            final Constructor<?> ctor = clazz.getDeclaredConstructor();
            ctor.setAccessible(true);
            return new ObjectConstructor() {
                public Object newInstance() {
                    try {
                        return ctor.newInstance((Object[])null);
                    } catch (InstantiationException e) {
                        throw new Sql2oException("Could not create a new instance of class " + clazz, e);
                    } catch (IllegalAccessException e) {
                        throw new Sql2oException("Could not create a new instance of class " + clazz, e);
                    } catch (InvocationTargetException e) {
                        throw new Sql2oException("Could not create a new instance of class " + clazz, e);
                    }
                }
            };
        } catch (Throwable e) {
            throw new Sql2oException("Could not find parameter-less constructor of class " + clazz, e);
        }
    }
}
