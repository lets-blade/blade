package org.sql2o.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class FactoryFacade {
    private final static FactoryFacade instance;

    static {
        MethodSetterFactory m;
        ObjectConstructorFactory o;
        try {
            m = (MethodSetterFactory) Class
                    .forName("org.sql2o.reflection.MethodAccessorsGenerator")
                    .newInstance();
            o = (ObjectConstructorFactory) m;
        } catch (Throwable ex) {
            m = new ReflectionMethodSetterFactory();
            o = null;
        }
        FieldSetterFactory f;
        try {
            Class<?> cls = Class.forName("org.sql2o.reflection.UnsafeFieldSetterFactory");
            f = (FieldSetterFactory) cls.newInstance();
            if(o==null) o = (ObjectConstructorFactory) f;
        } catch (Throwable ex) {
            f = new ReflectionFieldSetterFactory();
            o = new ReflectionObjectConstructorFactory();
        }
        instance = new FactoryFacade(f, m, o);
    }

    private final FieldSetterFactory fieldSetterFactory;
    private final MethodSetterFactory methodSetterFactory;
    private final ObjectConstructorFactory objectConstructorFactory;

    public FactoryFacade(FieldSetterFactory fieldSetterFactory, MethodSetterFactory methodSetterFactory, ObjectConstructorFactory objectConstructorFactory) {
        this.fieldSetterFactory = fieldSetterFactory;
        this.methodSetterFactory = methodSetterFactory;
        this.objectConstructorFactory = objectConstructorFactory;
    }

    public static FactoryFacade getInstance() {
        return instance;
    }

    public Setter newSetter(Field field) {
        return fieldSetterFactory.newSetter(field);
    }

    public Setter newSetter(Method method) {
        return methodSetterFactory.newSetter(method);
    }

    public ObjectConstructor newConstructor(Class<?> cls) {
        return objectConstructorFactory.newConstructor(cls);
    }
}

