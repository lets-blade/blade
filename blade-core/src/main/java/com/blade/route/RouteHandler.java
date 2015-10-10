package com.blade.route;

import com.blade.servlet.Request;
import com.blade.servlet.Response;

public interface RouteHandler {

	public Object handler(Request request, Response response);
	
}
