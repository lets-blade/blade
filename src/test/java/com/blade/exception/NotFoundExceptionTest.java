package com.blade.exception;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author biezhi
 * @date 2017/9/19
 */
public class NotFoundExceptionTest {

    @Test
    public void testNotFoundException() throws Exception {
        try {
            throw new NotFoundException();
        } catch (NotFoundException e) {
            assertEquals(e.getStatus(), 404);
            assertEquals(e.getName(), "Not Found");
        }
    }

    @Test
    public void testNotFoundExceptionWithMessage() throws Exception {
        try {
            throw new NotFoundException("the url not found");
        } catch (NotFoundException e) {
            assertEquals(e.getStatus(), 404);
            assertEquals(e.getName(), "Not Found");
            assertEquals(e.getMessage(), "the url not found");
        }
    }

}
