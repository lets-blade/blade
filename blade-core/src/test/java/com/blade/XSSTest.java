package com.blade;

import com.blade.verify.HTMLFilter;

public class XSSTest {

	public static void main(String[] args) {
		
		String string1 = "<script>alert('XSS')</script>";
		String string2 = "<img src=\"javascript:alert('XSS')\">";
		String string3 = "%0a%0a<script>alert(\"Vulnerable\")</script>.jsp";
		String string4 = "%22%3cscript%3ealert(%22xss%22)%3c/script%3e";
		String string5 = "%2e%2e/%2e%2e/%2e%2e/%2e%2e/%2e%2e/%2e%2e/%2e%2e/etc/passwd";
		String string6 = "%2E%2E/%2E%2E/%2E%2E/%2E%2E/%2E%2E/windows/win.ini";
		String string7 = "%3c/a%3e%3cscript%3ealert(%22xss%22)%3c/script%3e";
		
		System.out.println(HTMLFilter.htmlSpecialChars(string1));
		System.out.println(HTMLFilter.htmlSpecialChars(string2));
		System.out.println(HTMLFilter.htmlSpecialChars(string3));
		System.out.println(HTMLFilter.htmlSpecialChars(string4));
		System.out.println(HTMLFilter.htmlSpecialChars(string5));
		System.out.println(HTMLFilter.htmlSpecialChars(string6));
		System.out.println(HTMLFilter.htmlSpecialChars(string7));
		
	}
}
