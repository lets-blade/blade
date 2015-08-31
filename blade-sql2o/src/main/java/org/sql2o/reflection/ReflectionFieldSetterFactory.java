package org.sql2o.reflection;

import java.lang.reflect.Field;

/**
 * Created with IntelliJ IDEA.
 * User: dimzon
 * Date: 4/6/14
 * Time: 12:40 AM
 * To change this template use File | Settings | File Templates.
 */
public class ReflectionFieldSetterFactory implements FieldSetterFactory {
    public Setter newSetter(Field field) {
        return new FieldSetter(field);
    }
}
