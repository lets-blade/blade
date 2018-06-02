package com.blade.kit;

import com.blade.model.TestBean;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author biezhi
 *         2017/6/6
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

        TestBean bean = JsonKit.formJson(text, TestBean.class);
        System.out.println(bean);
    }

    @Test
    public void test2(){
        TestBean testBean = new TestBean();
        testBean.setDateTime(LocalDateTime.now());
        System.out.println(JsonKit.toString(testBean));
    }

    @Test
    public void testLocal(){
        Map<String,Object> result = new HashMap<>(8);
        result.put("date1", new Date());
        result.put("date2", LocalDate.now());
        result.put("date3", LocalDateTime.now());
        System.out.println(JsonKit.toString(result));
    }

}

