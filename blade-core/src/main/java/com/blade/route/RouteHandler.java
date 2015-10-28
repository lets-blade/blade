package com.blade.route;

import com.blade.http.Request;
import com.blade.http.Response;

/**
 * 路由执行器
 * @author biezhi
 *
 */
public interface RouteHandler {

	public void handle(Request request, Response response);
	
}
