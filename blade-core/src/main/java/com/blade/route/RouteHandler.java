package com.blade.route;

import com.blade.servlet.Request;
import com.blade.servlet.Response;

/**
 * 路由执行器
 * @author biezhi
 *
 */
public interface RouteHandler {

	public Object handle(Request request, Response response);
	
}
