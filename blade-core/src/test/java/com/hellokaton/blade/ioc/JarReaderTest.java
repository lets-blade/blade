package com.hellokaton.blade.ioc;

import com.hellokaton.blade.ioc.bean.ClassInfo;
import com.hellokaton.blade.ioc.bean.Scanner;
import com.hellokaton.blade.ioc.reader.JarReaderImpl;
import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

/**
 * @author biezhi
 * @date 2017/9/19
 */
public class JarReaderTest {

    @Test
    public void testJarReader() {
        JarReaderImpl  jarReader  = new JarReaderImpl();
        Set<ClassInfo> classInfos = jarReader.readClasses(Scanner.builder().packageName("org.slf4j.impl").build());
        Assert.assertNotNull(classInfos);
    }

}
