package org.sql2o.reflection;

import java.lang.reflect.Method;

/**
 * Created with IntelliJ IDEA.
 * User: dimzon
 * Date: 4/6/14
 * Time: 12:42 AM
 * To change this template use File | Settings | File Templates.
 */
public interface MethodSetterFactory {
    Setter newSetter(Method method);
}
