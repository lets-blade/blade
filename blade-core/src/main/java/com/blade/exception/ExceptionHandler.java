package com.blade.exception;

import com.blade.servlet.Request;
import com.blade.servlet.Response;

public interface ExceptionHandler {

	void handle(Exception e, Request request, Response response);
	
}
