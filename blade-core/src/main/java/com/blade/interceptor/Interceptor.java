package com.blade.interceptor;

import com.blade.web.http.Request;
import com.blade.web.http.Response;

public interface Interceptor {
	
	boolean before(Request request, Response response);
	
	boolean after(Request request, Response response);
	
}