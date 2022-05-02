package com.blade.exception;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author biezhi
 * @date 2017/9/19
 */
public class BladeExceptionTest {

    @Test
    public void testBladeException() throws Exception {
        try {
            throw new BadRequestException();
        } catch (BladeException e) {
            Assert.assertEquals(e.getStatus(), 400);
            Assert.assertEquals(e.getName(), "Bad Request");
        }

        try {
            throw new InternalErrorException();
        } catch (BladeException e) {
            Assert.assertEquals(e.getStatus(), 500);
            Assert.assertEquals(e.getName(), "Internal Error");
        }

    }

}
