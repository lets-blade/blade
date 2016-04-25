package com.blade.kit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import blade.kit.json.JSON;
import blade.kit.json.JSONArray;
import blade.kit.json.JSONHelper;
import blade.kit.json.JSONKit;
import blade.kit.json.JSONObject;
import blade.kit.json.JSONValue;
import org.junit.Assert;
import org.junit.Test;

public class JSONTest {

    @Test
    public void testModifyJsonObject() {
        JSONObject obj1 = new JSONObject();
        obj1.put("name", "jack");
        Assert.assertEquals(obj1.getString("name"), "jack");

        JSONArray obj2 = new JSONArray();
        obj2.add("123");

        Assert.assertEquals(obj2.toString(), "[\"123\"]");

        User u1 = new User();
        u1.setAge(22);
        u1.setName("rose");

        Assert.assertEquals(JSONHelper.toJSONValue(u1).toString(), "{\"name\":\"rose\",\"age\":22}");
    }

    @Test
    public void testParseJsonArray() {
        String json = "[{\"abc\":\"123456\",\"def\":\"hmm\"}]";
        List<JSONValue> list2 = JSON.parse(json).asArray().values();
        Assert.assertFalse(list2.isEmpty());

        JSONValue value = list2.get(0);
        JSONObject obj = value.asJSONObject();
        Assert.assertEquals(obj.getString("abc"), "123456");
        Assert.assertEquals(obj.getString("def"), "hmm");
    }

    @Test
    public void testToJSONString() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();

        map.put("abc", "123456");
        map.put("def", "hmm");
        list.add(map);

        String string = JSONKit.toJSONString(list);
        Assert.assertEquals(string, "[{\"abc\":\"123456\",\"def\":\"hmm\"}]");
    }
}
