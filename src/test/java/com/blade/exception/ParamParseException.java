package com.blade.exception;

import com.blade.BaseTestCase;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author biezhi
 * @date 2017/9/20
 */
public class ParamParseException extends BaseTestCase {

    @Test
    public void testByZeroException() throws Exception {
        start(
                app.get("/", (request, response) -> {
                    int a =1/0;
                }).exceptionHandler(e -> Assert.assertEquals(ArithmeticException.class, e.getClass()))
        );
        bodyToString("/");
    }

    @Test
    public void testNumberFormatException() throws Exception {
        start(
                app.get("/", (request, response) -> request.queryInt("age"))
                        .exceptionHandler(e -> {
                            Assert.assertEquals(NumberFormatException.class, e.getClass());
                        })
        );
        bodyToString("/?age=abc");
    }

}
