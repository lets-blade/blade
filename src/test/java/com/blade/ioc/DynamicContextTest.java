package com.blade.ioc;

import com.blade.ioc.reader.ClassPathClassReader;
import com.blade.ioc.reader.ClassReader;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author biezhi
 * @date 2017/9/19
 */
public class DynamicContextTest {

    @Test
    public void testDynamicContext() {
        ClassReader classReader = DynamicContext.getClassReader("com.blade.ioc");
        assertEquals(ClassPathClassReader.class, classReader.getClass());
        assertEquals(false, DynamicContext.isJarPackage("com.blade.ioc"));
        assertNotNull(DynamicContext.getClassReader("io.netty.handler.codec"));
        assertEquals(true, DynamicContext.isJarPackage("io.netty.handler.codec"));
        assertEquals(false, DynamicContext.isJarPackage(""));
        assertEquals(false, DynamicContext.isJarContext());
    }

    @Test
    public void testInit() {
        DynamicContext.init(DynamicContext.class);
    }
}