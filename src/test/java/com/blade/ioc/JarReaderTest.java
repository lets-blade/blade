package com.blade.ioc;

import com.blade.ioc.reader.ClassInfo;
import com.blade.ioc.reader.JarReaderImpl;
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
        Set<ClassInfo> classInfos = jarReader.getClass("org.slf4j.impl", false);
        Assert.assertNotNull(classInfos);
    }

}
