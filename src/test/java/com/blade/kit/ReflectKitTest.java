package com.blade.kit;

import com.blade.types.BladeBeanDefineType;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author biezhi
 * @date 2017/9/20
 */
public class ReflectKitTest {

    @Test
    public void testConvert() {
        Object o1 = ReflectKit.convert(String.class, "hello");
        Assert.assertEquals("hello", o1);

        Object o2 = ReflectKit.convert(BigDecimal.class, "20.1");
        Assert.assertEquals(new BigDecimal("20.1"), o2);

        Object o3 = ReflectKit.convert(Float.class, "2.2");
        Assert.assertEquals(Float.valueOf("2.2"), o3);

        Object o4 = ReflectKit.convert(Date.class, "2017-09-09");
        Assert.assertEquals(Date.class, o4.getClass());

        Object o5 = ReflectKit.convert(LocalDate.class, "2017-09-09");
        Assert.assertEquals(LocalDate.class, o5.getClass());

        Object o6 = ReflectKit.convert(LocalDateTime.class, "2017-09-09 21:22:33");
        Assert.assertEquals(LocalDateTime.class, o6.getClass());
    }

    @Test
    public void testIsPrimitive() {
        Assert.assertEquals(true, ReflectKit.isPrimitive(int.class));
        Assert.assertEquals(true, ReflectKit.isPrimitive(Integer.class));
        Assert.assertEquals(false, ReflectKit.isPrimitive(Date.class));
        Assert.assertEquals(false, ReflectKit.isPrimitive(BigDecimal.class));

        Assert.assertEquals(true, ReflectKit.isPrimitive(20));
        Assert.assertEquals(true, ReflectKit.isPrimitive(true));
        Assert.assertEquals(true, ReflectKit.isPrimitive(1.2D));
        Assert.assertEquals(true, ReflectKit.isPrimitive(1.2f));

        Assert.assertEquals(false, ReflectKit.isPrimitive(new BigDecimal("22")));
    }

    @Test
    public void testIs() {
        Assert.assertEquals(true, ReflectKit.is(22, Integer.valueOf(22)));
        Assert.assertEquals(true, ReflectKit.is(22L, Long.valueOf(22)));
        Assert.assertEquals(true, ReflectKit.is(true, Boolean.valueOf(true)));

        Assert.assertEquals(false, ReflectKit.is(22, Long.valueOf(22)));
    }

    @Test
    public void testIsGetMethod() {
        Method method = ReflectKit.getMethod(BladeBeanDefineType.class, "hello", String.class);
        Assert.assertNotNull(method);

        Method method2 = ReflectKit.getMethod(BladeBeanDefineType.class, "hello2", String.class);
        Assert.assertNull(method2);
    }

}
