package com.hellokaton.blade.ioc;

import com.hellokaton.blade.ioc.bean.ClassInfo;
import com.hellokaton.blade.ioc.bean.Scanner;
import com.hellokaton.blade.ioc.reader.ClassPathClassReader;
import com.hellokaton.blade.annotation.Path;
import com.hellokaton.blade.types.controller.IndexController;
import com.hellokaton.blade.types.controller.UserService;
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

        String packageName = "com.hellokaton.blade.types.controller";

        ClassReader classReader = DynamicContext.getClassReader(packageName);
        assertEquals(ClassPathClassReader.class, classReader.getClass());

        Set<ClassInfo> classInfos = classReader.readClasses(Scanner.builder().packageName(packageName).build());
        assertEquals(2, classInfos.size());

        classInfos = classReader.readClasses(Scanner.builder().packageName(packageName).parent(Runnable.class).build());
        assertEquals(1, classInfos.size());
        assertEquals(UserService.class, classInfos.stream().findFirst().get().getClazz());

        classInfos = classReader.readClasses(Scanner.builder().packageName(packageName).annotation(Path.class).build());
        assertEquals(1, classInfos.size());
        assertEquals(IndexController.class, classInfos.stream().findFirst().get().getClazz());

        classInfos = classReader.readClasses(Scanner.builder().packageName(packageName).parent(Object.class).annotation(Path.class).build());
        assertEquals(1, classInfos.size());
        assertEquals(IndexController.class, classInfos.stream().findFirst().get().getClazz());

    }

}
