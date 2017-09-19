package com.blade.ioc;

import com.blade.ioc.reader.ClassInfo;
import com.blade.types.BladeClassDefineType;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author biezhi
 * @date 2017/9/19
 */
public class ClassInfoTest {

    @Test
    public void testClassInfo() {
        String clsName = "com.blade.types.BladeClassDefineType";

        ClassInfo classInfo = new ClassInfo(clsName);
        Assert.assertEquals(null, classInfo.getClazz());
        Assert.assertEquals(BladeClassDefineType.class.getName(), classInfo.getClassName());

        classInfo = new ClassInfo(BladeClassDefineType.class);
        Assert.assertEquals(BladeClassDefineType.class, classInfo.getClazz());

        classInfo = new ClassInfo(clsName, BladeClassDefineType.class);
        Assert.assertEquals(BladeClassDefineType.class, classInfo.getClazz());

        Object object = classInfo.newInstance();
        Assert.assertNotNull(object);
    }

}
