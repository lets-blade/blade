package com.blade.event;

import com.blade.BaseTestCase;
import org.junit.Test;

/**
 * @author biezhi
 * @date 2017/9/19
 */
public class BeanProcessorTest extends BaseTestCase {

    @Test
    public void testBeanProcessor(){
        start(
                app.onStarted(blade -> System.out.println("Blade started"))
        );
    }

}
