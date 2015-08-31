package org.sql2o.reflection;

import org.sql2o.Sql2oException;
import sun.reflect.ConstructorAccessor;
import sun.reflect.FieldAccessor;
import sun.reflect.MethodAccessor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressWarnings("UnusedDeclaration")
public class MethodAccessorsGenerator implements MethodSetterFactory, ObjectConstructorFactory {
    private static final ThreadLocal<Object> generatorObjectHolder;
    private static final MethodAccessor generateMethod;
    private static final MethodAccessor generateConstructor;
    private static final MethodAccessor generateSerializationConstructor;
    private static final MethodAccessor newFieldAccessor;

    static {
        try {
            Class<?> aClass = Class.forName("sun.reflect.MethodAccessorGenerator");
            Constructor<?>[] declaredConstructors = aClass.getDeclaredConstructors();
            Constructor<?> declaredConstructor = declaredConstructors[0];
            declaredConstructor.setAccessible(true);
            Object generatorObject = declaredConstructor.newInstance();
            Method bar = aClass.getMethod("generateMethod", Class.class, String.class, Class[].class, Class.class, Class[].class, Integer.TYPE);
            bar.setAccessible(true);
            generateMethod = (MethodAccessor) bar.invoke(
                    generatorObject,
                    bar.getDeclaringClass(),
                    bar.getName(),
                    bar.getParameterTypes(),
                    bar.getReturnType(),
                    bar.getExceptionTypes(),
                    bar.getModifiers());
            bar = aClass.getMethod("generateConstructor", Class.class, Class[].class, Class[].class, Integer.TYPE);
            generateConstructor = newMethodAccessor(generatorObject, bar);
            bar = aClass.getMethod("generateSerializationConstructor", Class.class, Class[].class, Class[].class, Integer.TYPE, Class.class);
            final ConstructorAccessor goc = newConstructorAccessor(generatorObject, declaredConstructor);
            generatorObjectHolder = new ThreadLocal<Object>() {
                @Override
                protected Object initialValue() {
                    try {
                        return goc.newInstance(null);
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                }
            };
            generateSerializationConstructor = newMethodAccessor(generatorObject, bar);
            aClass = Class.forName("sun.reflect.UnsafeFieldAccessorFactory");
            bar = aClass.getDeclaredMethod("newFieldAccessor", Field.class, Boolean.TYPE);
            newFieldAccessor = newMethodAccessor(generatorObject, bar);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static FieldAccessor newFieldAccessor(Field field, boolean overrideFinalCheck) {
        try {
            return (FieldAccessor) newFieldAccessor.invoke(null, new Object[]{field, overrideFinalCheck});
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static MethodAccessor newMethodAccessor(Method bar) {
        try {
            return newMethodAccessor(generatorObjectHolder.get(), bar);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static MethodAccessor newMethodAccessor(Object generatorObject, Method bar) throws InvocationTargetException {
        return (MethodAccessor) generateMethod.invoke(
                generatorObject, new Object[]{
                bar.getDeclaringClass(),
                bar.getName(),
                bar.getParameterTypes(),
                bar.getReturnType(),
                bar.getExceptionTypes(),
                bar.getModifiers()});
    }

    public static ConstructorAccessor newConstructorAccessor(Constructor<?> bar) {
        try {
            return newConstructorAccessor(generatorObjectHolder.get(), bar);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static ConstructorAccessor newConstructorAccessor(Object generatorObject, Constructor<?> bar) throws InvocationTargetException {
        return (ConstructorAccessor) generateConstructor.invoke(
                generatorObject, new Object[]{
                bar.getDeclaringClass(),
                bar.getParameterTypes(),
                bar.getExceptionTypes(),
                bar.getModifiers()});
    }

    public static ConstructorAccessor newConstructorAccessor(Constructor<?> bar, Class<?> targetClass) {
        try {
            return (ConstructorAccessor) generateSerializationConstructor.invoke(
                    generatorObjectHolder.get(), new Object[]{
                    targetClass,
                    bar.getParameterTypes(),
                    bar.getExceptionTypes(),
                    bar.getModifiers(),
                    bar.getDeclaringClass()});
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public Setter newSetter(final Method method) {
        final Class type = method.getParameterTypes()[0];
        final MethodAccessor methodAccessor = newMethodAccessor(method);
        return new Setter() {
            public void setProperty(Object obj, Object value) {
                if (value == null && type.isPrimitive()) return;
                try {
                    methodAccessor.invoke(obj, new Object[]{value});
                } catch (InvocationTargetException e) {
                    throw new Sql2oException("error while calling setter method with name " + method.getName() + " on class " + obj.getClass().toString(), e);
                }
            }

            public Class getType() {
                return type;
            }
        };
    }

    @Override
    public ObjectConstructor newConstructor(final Class<?> cls) {
        for (Class<?> cls0 = cls; cls != Object.class; cls0 = cls0.getSuperclass()) {
            try {
                Constructor<?> ctor = cls0.getDeclaredConstructor();
                final ConstructorAccessor constructorAccessor = (cls0 == cls)
                        ? newConstructorAccessor(ctor)
                        : newConstructorAccessor(ctor, cls);
                return new ObjectConstructor() {
                    @Override
                    public Object newInstance() {
                        try {
                            return constructorAccessor.newInstance(null);
                        } catch (InstantiationException e) {
                            throw new Sql2oException("Could not create a new instance of class " + cls, e);
                        } catch (InvocationTargetException e) {
                            throw new Sql2oException("Could not create a new instance of class " + cls, e);
                        }
                    }
                };
            } catch (NoSuchMethodException e) {
                // ignore
            }
        }
        return UnsafeFieldSetterFactory.getConstructor(cls);
    }
}