package com.blade.exception;

import com.blade.mvc.hook.Signature;

/**
 * Global exception handle
 * <p>
 * Created by biezhi on 10/07/2017.
 */
@FunctionalInterface
public interface ExceptionResolve {

    boolean handle(Exception e, Signature signature);

}
