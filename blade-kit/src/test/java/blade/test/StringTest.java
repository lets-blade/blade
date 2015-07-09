package blade.test;

import java.util.ArrayList;
import java.util.List;

import blade.kit.StringKit;

public class StringTest {

	public static void main(String[] args) {
		String[] a = {"/aa/", "/aa/cc"};
		
		String bString= StringKit.join(a, "|");
		System.out.println(bString);
		
		
		List<String> list = new ArrayList<String>();
		list.add("aa2@qq.com");
		list.add("a3a@qq.com");
		list.add("aa4@qq.com");
		
	}
}
