package com.hellokaton.blade.ioc;

import com.hellokaton.blade.Blade;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author : ccqy66
 * Date: 2017/12/25
 */
public class ValueDefineTest {

    private Blade app;

    @Before
    public void before() {
        app = Blade.create();
        app.scanPackages("com.hellokaton.blade.model","com.hellokaton.blade.ioc");
        app.listen(10087).start().await();
    }

    @After
    public void after() {
        app.stop();
    }

    @Test
    public void testValueFromClass() {
        Assert.assertNotNull(app.ioc().getBean("com.hellokaton.blade.model.AppInfo"));
        System.out.println(app.ioc().getBean("com.hellokaton.blade.model.AppInfo"));
        //echo AppInfo{users='301', maxMoney='38.1', sex='true', hits='199283818033', startDate='2017-08-02'}
    }

    @Test
    public void testValueFromPro() {
        Assert.assertNotNull(app.ioc().getBean("com.hellokaton.blade.model.ValueBean"));
        System.out.println(app.ioc().getBean("com.hellokaton.blade.model.ValueBean"));
        //echo ValueBean{list=[hello, my, blade, word], appversion='0.0.2', map={user=blade, version=2.0.0}}
    }

}
