package com.blade.web.verify;

import blade.kit.text.HTMLFilter;

public class Xss {
	
	public String filter(String str){
		return HTMLFilter.htmlSpecialChars(str);
	}
	
}
