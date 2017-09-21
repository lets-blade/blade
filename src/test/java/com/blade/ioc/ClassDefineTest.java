package com.blade.ioc;

import com.blade.ioc.annotation.Bean;
import com.blade.types.BladeClassDefineType;
import com.blade.types.BladeWebHookType;
import org.junit.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author biezhi
 * @date 2017/9/19
 */
public class ClassDefineTest {

    @Test
    public void testClassDefine() {
        ClassDefine       classDefine = ClassDefine.create(BladeClassDefineType.class);
        int               modifires   = classDefine.getModifiers();
        Field[]           fields      = classDefine.getDeclaredFields();
        Bean              bean        = classDefine.getAnnotation(Bean.class);
        Annotation[]      annotations = classDefine.getAnnotations();
        List<ClassDefine> interfaces  = classDefine.getInterfaces();
        String            name        = classDefine.getName();
        String            simpleName  = classDefine.getSimpleName();
        ClassDefine       superKlass  = classDefine.getSuperKlass();
        Class<?>          type        = classDefine.getType();

        assertEquals(Modifier.PUBLIC, modifires);
        assertEquals(2, fields.length);
        assertNotNull(bean);
        assertEquals(1, annotations.length);
        assertEquals(0, interfaces.size());
        assertEquals("com.blade.types.BladeClassDefineType", name);
        assertEquals("BladeClassDefineType", simpleName);
        assertEquals(Object.class, superKlass.getType());
        assertEquals(BladeClassDefineType.class, type);
        assertEquals(false, classDefine.isAbstract());
        assertEquals(false, classDefine.isInterface());
        assertEquals(false, classDefine.isStatic());
        assertEquals(true, classDefine.isPublic());
        assertEquals(false, classDefine.isPrivate());
        assertEquals(false, classDefine.isProtected());

        assertEquals(1, ClassDefine.create(BladeWebHookType.class).getInterfaces().size());
        assertEquals(1, ClassDefine.create(BladeWebHookType.class).getInterfaces().size());

    }


}
