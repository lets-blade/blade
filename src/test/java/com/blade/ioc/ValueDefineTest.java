package com.blade.ioc;

import com.blade.Blade;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author : ccqy66
 * Date: 2017/12/25
 */
public class ValueDefineTest {
    Blade app = Blade.me();
    @Before
    public void before() {
        app.scanPackages("com.blade.model","com.blade.ioc");
        app.listen(10086).start().await();
    }
    @Test
    public void testValueFromClass() {
        Assert.assertNotNull(app.ioc().getBean("com.blade.model.AppInfo"));
        System.out.println(app.ioc().getBean("com.blade.model.AppInfo"));
        //echo AppInfo{users='301', maxMoney='38.1', sex='true', hits='199283818033', startDate='2017-08-02'}
    }
    @Test
    public void testValueFromPro() {
        Assert.assertNotNull(app.ioc().getBean("com.blade.model.ValueBean"));
        System.out.println(app.ioc().getBean("com.blade.model.ValueBean"));
        //echo ValueBean{list=[hello, my, blade, word], appversion='0.0.2', map={user=blade, version=2.0.0}}
    }
}
