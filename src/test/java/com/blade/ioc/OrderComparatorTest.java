package com.blade.ioc;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author biezhi
 * @date 2017/9/21
 */
public class OrderComparatorTest {

    @Test
    public void testOrder(){
        OrderComparator orderComparator = new OrderComparator();
        int             compare         = orderComparator.compare("a", "b");
        Assert.assertEquals(0, compare);
    }

}
