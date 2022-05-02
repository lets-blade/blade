package com.blade.exception;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author biezhi
 * @date 2017/9/19
 */
public class TemplateExceptionTest {

    @Test
    public void testTemplateException() throws Exception {
        try {
            throw new TemplateException("not found template");
        } catch (TemplateException e) {
            assertEquals("not found template", e.getMessage());
        }
    }

}
