package com.blade.ioc;

import com.blade.types.BladeBeanDefineType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author biezhi
 * @date 2017/9/19
 */
public class BeanDefineTest {

    @Test
    public void testBeanDefine() {
        BeanDefine beanDefine = new BeanDefine(new BladeBeanDefineType());
        Class<?>   type       = beanDefine.getType();
        assertEquals(BladeBeanDefineType.class, type);

        Object bean = beanDefine.getBean();
        assertNotNull(bean);
        assertEquals(true, beanDefine.isSingle());

        beanDefine.setSignle(true);
        beanDefine.setType(BladeBeanDefineType.class);
        beanDefine.setBean(new BladeBeanDefineType());

        assertEquals(BladeBeanDefineType.class, type);
        assertNotNull(bean);
        assertEquals(true, beanDefine.isSingle());

    }

    @Test
    public void testBeanDefine2() {
        BeanDefine beanDefine = new BeanDefine(new BladeBeanDefineType(), BladeBeanDefineType.class);
        assertEquals(BladeBeanDefineType.class, beanDefine.getType());

        beanDefine = new BeanDefine(new BladeBeanDefineType(), BladeBeanDefineType.class, true);
        assertEquals(BladeBeanDefineType.class, beanDefine.getType());
        assertEquals(true, beanDefine.isSingle());
    }

}
