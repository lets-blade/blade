package com.hellokaton.blade.ioc;

import com.hellokaton.blade.ioc.reader.ClassPathClassReader;
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
        ClassReader classReader = DynamicContext.getClassReader("com.hellokaton.blade.ioc");
        assertEquals(ClassPathClassReader.class, classReader.getClass());
        assertEquals(false, DynamicContext.isJarPackage("com.hellokaton.blade.ioc"));
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