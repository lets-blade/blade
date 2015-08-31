package org.sql2o.reflection;

/**
 * Created with IntelliJ IDEA.
 * User: dimzon
 * Date: 4/6/14
 * Time: 1:27 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ObjectConstructorFactory {
    ObjectConstructor newConstructor(Class<?> cls);
}
