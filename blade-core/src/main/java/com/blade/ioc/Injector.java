package com.blade.ioc;

/**
 * Bean Injector interface
 *
 * @author <a href="mailto:hellokaton@gmail.com" target="_blank">hellokaton</a>
 * @since 1.5
 */
public interface Injector {

    /**
     * Injection bean
     *
     * @param bean bean instance
     */
    void injection(Object bean);

    void injection(Object bean, Object value);

}