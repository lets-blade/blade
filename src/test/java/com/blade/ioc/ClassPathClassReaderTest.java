package com.blade.ioc;

import com.blade.ioc.reader.ClassInfo;
import com.blade.ioc.reader.ClassPathClassReader;
import com.blade.ioc.reader.ClassReader;
import com.blade.mvc.annotation.Path;
import com.blade.types.controller.IndexController;
import com.blade.types.controller.UserService;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * @author biezhi
 * @date 2017/9/19
 */
public class ClassPathClassReaderTest {

    @Test
    public void testClassPathReader() {

        String packageName = "com.blade.types.controller";

        ClassReader classReader = DynamicContext.getClassReader(packageName);
        assertEquals(ClassPathClassReader.class, classReader.getClass());

        Set<ClassInfo> classInfos = classReader.getClass(packageName, false);
        assertEquals(2, classInfos.size());

        classInfos = classReader.getClass(packageName, Runnable.class, false);
        assertEquals(1, classInfos.size());
        assertEquals(UserService.class, classInfos.stream().findFirst().get().getClazz());

        classInfos = classReader.getClassByAnnotation(packageName, Path.class, false);
        assertEquals(1, classInfos.size());
        assertEquals(IndexController.class, classInfos.stream().findFirst().get().getClazz());

        classInfos = classReader.getClassByAnnotation(packageName, Object.class, Path.class, false);
        assertEquals(1, classInfos.size());
        assertEquals(IndexController.class, classInfos.stream().findFirst().get().getClazz());

    }

}
