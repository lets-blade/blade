
package com.blade.ioc.reader;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * 一个类读取器的接口
 *
 * @author <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.0
 */
public interface ClassReader {

    /**
     * Get class list by package
     *
     * @param packageName package name
     * @param recursive   whether the recursive scanning
     * @return return the scanning to all classes
     */
    Set<ClassInfo> getClass(String packageName, boolean recursive);

    /**
     * Get class list by package and parent class
     *
     * @param packageName package name
     * @param parent      parent class
     * @param recursive   whether the recursive scanning
     * @return return the scanning to all classes
     */
    Set<ClassInfo> getClass(String packageName, Class<?> parent, boolean recursive);

    /**
     * Get class list by annotation
     *
     * @param packageName package name
     * @param annotation  class annotation
     * @param recursive   whether the recursive scanning
     * @return return the scanning to all classes
     */
    Set<ClassInfo> getClassByAnnotation(String packageName, Class<? extends Annotation> annotation, boolean recursive);

    /**
     * Get class list by annotation and parent class
     *
     * @param packageName package name
     * @param parent      parent class
     * @param annotation  class annotation
     * @param recursive   whether the recursive scanning
     * @return return the scanning to all classes
     */
    Set<ClassInfo> getClassByAnnotation(String packageName, Class<?> parent, Class<? extends Annotation> annotation, boolean recursive);

}