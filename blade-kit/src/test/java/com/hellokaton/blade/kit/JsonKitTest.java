package com.hellokaton.blade.kit;

import com.hellokaton.blade.kit.model.ChildBean;
import com.hellokaton.blade.kit.model.TestBean;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author biezhi
 * 2017/6/6
 */
public class JsonKitTest {

    @Test
    public void test1() throws Exception {
        TestBean testBean = new TestBean();
        testBean.setAge(20);
        testBean.setName("jack");
        testBean.setPrice(2.31D);
        testBean.setSex(false);
        testBean.setOtherList(new String[]{"a", "b"});

        String text = JsonKit.toString(testBean);
        System.out.println(text);

        TestBean bean = JsonKit.fromJson(text, TestBean.class);
        System.out.println(bean);
    }

    @Test
    public void test2() {
        TestBean testBean = new TestBean();
        testBean.setDateTime(LocalDateTime.now());
        System.out.println(JsonKit.toString(testBean));
    }

    @Test
    public void testLocal() {
        Map<String, Object> result = new HashMap<>(8);
        result.put("date1", new Date());
        result.put("date2", LocalDate.now());
        result.put("date3", LocalDateTime.now());
        System.out.println(JsonKit.toString(result));
    }

    @Test
    public void test3() {
        TestBean testBean = new TestBean();
        testBean.setName("\"hello\"_world");
        System.out.println(JsonKit.toString(testBean));
    }

    @Test
    public void test4() {
        ChildBean childBean = new ChildBean();
        childBean.setSuperField("super");
        childBean.setChildField("child");
        childBean.setRepeatField("sss");
        String json = JsonKit.toString(childBean);

        Map<String, Object> expectedMap = JsonKit.fromJson("{\"repeatField\":\"sss\",\"childField\":\"child\",\"superField\":\"super\"}", Map.class);
        Map<String, Object> actualMap = JsonKit.fromJson(json, Map.class);
        Assert.assertEquals(expectedMap, actualMap);

        ChildBean formJson = JsonKit.fromJson(json, ChildBean.class);

        Assert.assertEquals(formJson, childBean);
    }

}

