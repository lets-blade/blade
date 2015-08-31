package org.sql2o.reflection;

import java.lang.reflect.Field;

/**
 * Created with IntelliJ IDEA.
 * User: dimzon
 * Date: 4/6/14
 * Time: 12:39 AM
 * To change this template use File | Settings | File Templates.
 */
public interface FieldSetterFactory {
    Setter newSetter(Field field);
}
