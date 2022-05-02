package com.blade.kit;

import org.junit.Test;

/**
 * @author biezhi
 * @date 2017/9/20
 */
public class AssertTest {

    @Test(expected = IllegalArgumentException.class)
    public void testNotEmpty() {
        Assert.notEmpty("", "Not Empty");
        Assert.notEmpty(new String[]{}, "Arr Empty");
        Assert.notNull(null, "Not null");
    }

    @Test(expected = RuntimeException.class)
    public void testWrap() {
        Assert.wrap(() -> {
            int a = 1 / 0;
            return true;
        });
    }

    @Test
    public void testWrap2() {
        boolean flag = Assert.wrap(() -> true);
        org.junit.Assert.assertEquals(true, flag);
    }

}
