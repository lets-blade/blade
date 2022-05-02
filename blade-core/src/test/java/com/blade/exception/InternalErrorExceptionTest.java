package com.blade.exception;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author biezhi
 * @date 2017/9/19
 */
public class InternalErrorExceptionTest {

    @Test
    public void testInternalErrorException() throws Exception {
        try {
            throw new InternalErrorException();
        } catch (InternalErrorException e) {
            assertEquals(e.getStatus(), 500);
            assertEquals(e.getName(), "Internal Error");
        }
    }

    @Test
    public void testInternalErrorExceptionWithMessage() throws Exception {
        try {
            throw new InternalErrorException("param [name] not is empty");
        } catch (InternalErrorException e) {
            assertEquals(e.getStatus(), 500);
            assertEquals(e.getName(), "Internal Error");
            assertEquals(e.getMessage(), "param [name] not is empty");
        }
    }

}