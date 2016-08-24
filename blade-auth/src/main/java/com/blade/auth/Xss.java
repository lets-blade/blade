package com.blade.auth;

import com.blade.kit.text.HTMLFilter;

public class Xss {
	
	public String filter(String str){
		return HTMLFilter.htmlSpecialChars(str);
	}
	
}
