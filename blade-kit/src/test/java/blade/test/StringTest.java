package blade.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import blade.kit.StringKit;


public class StringTest {

	public static void main(String[] args) {
		
		//判断是否为空
		String str = "hello";
		System.out.println(StringKit.isNotEmpty(str));
		
		//分割字符串
		String[] arr = StringKit.split("1,2,3", ",");
		System.out.println(Arrays.toString(arr));
		
		//生成5个随机字符串
		System.out.println(StringKit.random(5));
		
		//将字符串20转换为long类型，如果为null或者空则给一个默认值10
		System.out.println(StringKit.toLong("20", 10));
		
		List<Integer> list = new ArrayList<Integer>();
		list.add(22);
		list.add(30);
		//将集合用指定字符分隔
		String listString = StringKit.join(list, "|");
		System.out.println(listString);
	}
	
}
