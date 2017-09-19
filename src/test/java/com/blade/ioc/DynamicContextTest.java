package com.blade.ioc;

import com.blade.ioc.reader.ClassPathClassReader;
import com.blade.ioc.reader.ClassReader;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

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
        assertEquals(true, DynamicContext.isJarPackage("io.netty.handler.codec"));
        assertEquals(false, DynamicContext.isJarContext());
    }

}
