package com.blade.mvc.ui;

import com.blade.mvc.ui.template.BladeTemplate;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author biezhi
 * @date 2018/3/23
 */
public class BladeTemplateTest {

    @Test
    public void testFmt(){
        String body = "Hello ${name}";
        Map<String, Object> attributes = new HashMap<>();
        String result = BladeTemplate.template(body, attributes).fmt();
        Assert.assertEquals("Hello ", result);

        attributes.put("name", "jack");
        result = BladeTemplate.template(body, attributes).fmt();
        Assert.assertEquals("Hello jack", result);

        body = "Hello, My Name is ${user.username} and age is ${user.age}";
        Map<String, Object> user = new HashMap<>();
        user.put("age", 22);
        user.put("username", "biezhi");

        attributes.put("user", user);

        result = BladeTemplate.template(body, attributes).fmt();
        Assert.assertEquals("Hello, My Name is biezhi and age is 22", result);
    }


}
