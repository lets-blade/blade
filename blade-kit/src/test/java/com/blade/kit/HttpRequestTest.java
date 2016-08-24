package com.blade.kit;

import java.io.File;

import com.blade.kit.http.HttpRequest;

public class HttpRequestTest {
	
	public static void main(String[] args) {
		
		// 发送一个GET请求并获取内容
		String body = HttpRequest.get("http://bladejava.com").body();
		System.out.println(body);
		
		// 发送一个带heder的请求
		String res = HttpRequest.get("http://bladejava.com").accept("application/json")
				.userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:39.0) Gecko/20100101 Firefox/39.0").body();
		System.out.println(res);
		
		// 发送一个POST请求
		HttpRequest.post("http://www.example.com").part("id", 20).part("name", "jack").body();
		
		// 带认证的请求
		int response = HttpRequest.get("http://google.com").basic("username", "p4ssw0rd").code();
		System.out.println(response);
		
		// 下载一个图片
		File file = new File("F:/a.png");
		HttpRequest.get("http://img.blog.csdn.net/20150601232126808").receive(file);
		
	}
	
}
