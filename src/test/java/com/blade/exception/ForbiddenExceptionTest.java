package com.blade.exception;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author biezhi
 * @date 2017/9/19
 */
public class ForbiddenExceptionTest {

    @Test
    public void testForbiddenException() throws Exception {
        try {
            throw new ForbiddenException();
        } catch (ForbiddenException e) {
            assertEquals(e.getStatus(), 403);
            assertEquals(e.getName(), "Forbidden");
        }
    }

    @Test
    public void testForbiddenExceptionWithMessage() throws Exception {
        try {
            throw new ForbiddenException("there is no access to");
        } catch (ForbiddenException e) {
            assertEquals(e.getStatus(), 403);
            assertEquals(e.getName(), "Forbidden");
            assertEquals(e.getMessage(), "there is no access to");
        }
    }

}
