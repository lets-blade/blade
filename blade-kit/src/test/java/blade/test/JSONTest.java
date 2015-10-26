package blade.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import blade.kit.json.JSONKit;
import blade.kit.json.Json;
import blade.kit.json.JsonArray;
import blade.kit.json.JsonObject;
import blade.kit.json.JsonValue;

public class JSONTest {

	public static void main(String[] args) {
		//[{"text": "首页","href": "/"},{"text": "博客","href": "blog"},{"text": "关于","href": "about"},{"text": "联系","href": "contact"}]
		String json = "[{\"text\": \"博客\",\"href\": \"blog\"}]";
		
		// 下面构造两个map、一个list和一个Employee对象
        Map<String, String> map1 = new HashMap<String, String>();
        map1.put("name", "Alexia");
        map1.put("sex", "female");
        map1.put("age", "23");

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();

        map.put("abc", "123456");
        map.put("def", "hmm");
        list.add(map);	
        
        String string = JSONKit.toJSONString(list);
        System.out.println(string);
        
		List<JsonValue> list2 = Json.parse(json).asArray().values();
		System.out.println(list2);
        
        JsonObject obj1 = new JsonObject();
        obj1.add("name", "jack");
        
        System.out.println(obj1);
        
        JsonArray obj2 = new JsonArray();
        obj2.add("123");
        
        System.out.println(obj2);
        
        User u1 = new User();
        u1.setAge(22);
        u1.setName("rose");
        
        System.out.println(JSONKit.toJSONString(u1));
        
//        User u = JSONKit.parse("{\"name\":\"jack\",\"age\":20}", User.class);
//        System.out.println(u);
	}
	
}
