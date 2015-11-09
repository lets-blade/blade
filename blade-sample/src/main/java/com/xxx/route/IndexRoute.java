package com.xxx.route;

import com.blade.http.Request;
import com.blade.http.Response;

public class IndexRoute {
	
	public void index(Request request, Response response){
		response.render("index");
	}
	
}
