package com.blade.event;

import com.blade.BaseTestCase;
import com.blade.Blade;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author biezhi
 * @date 2017/9/19
 */
public class BeanProcessorTest extends BaseTestCase {

    @Test
    public void testBeanProcessor(){
        Blade blade = mockBlade();
        BeanProcessor beanProcessor = mock(BeanProcessor.class);
        beanProcessor.processor(blade);
        verify(beanProcessor).processor(blade);
    }

}
