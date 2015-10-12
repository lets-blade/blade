package com.blade;

import com.blade.servlet.Request;
import com.blade.servlet.Response;

public class Index {

	public void hello(Request request, Response response){
		System.out.println("请求hello");
		response.html("<font color='red'>hi~</font>");
	}
	
}
