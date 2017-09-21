package com.blade.ioc;

import com.blade.types.BladeBeanDefineType;
import org.junit.Test;

import java.lang.reflect.Field;

/**
 * @author biezhi
 * @date 2017/9/21
 */
public class FieldInjectorTest {

    @Test
    public void testFieldInjector(){
        Ioc   ioc = new SimpleIoc();
        ioc.addBean("jack");
        ioc.addBean(new BladeBeanDefineType());
        Field field = BladeBeanDefineType.class.getDeclaredFields()[0];
        FieldInjector fieldInjector = new FieldInjector(ioc, field);
        fieldInjector.injection(ioc.getBean(BladeBeanDefineType.class));
    }

    @Test(expected = RuntimeException.class)
    public void testFieldInjectorError(){
        Ioc   ioc = new SimpleIoc();
        FieldInjector fieldInjector = new FieldInjector(ioc, null);
        fieldInjector.injection(new BladeBeanDefineType());
    }

}
