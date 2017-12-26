package com.blade.ioc;

import com.blade.BaseTestCase;
import org.junit.Test;

/**
 * @author : ccqy66
 * Date: 2017/12/25
 */
public class ValueDefineTest extends BaseTestCase {
    @Test
    public void testValueFromClass() {
        app.scanPackages("com.blade.model","com.blade.ioc");
        start(app);
        System.out.println(app.ioc().getBean("com.blade.model.AppInfo"));
        //echo AppInfo{users='301', maxMoney='38.1', sex='true', hits='199283818033', startDate='2017-08-02'}
    }
    @Test
    public void testValueFromPro() {
        app.scanPackages("com.blade.model","com.blade.ioc");
        start(app);
        System.out.println(app.ioc().getBean("com.blade.model.ValueBean"));
        //echo ValueBean{list=[hello, my, blade, word], appversion='0.0.2', map={user=blade, version=2.0.0}}
    }
}
