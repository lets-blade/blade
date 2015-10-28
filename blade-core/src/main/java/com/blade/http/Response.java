package com.blade.http;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import com.blade.render.ModelAndView;

public interface Response {

	HttpServletResponse raw();
	
	int status();

	Response status(int status);

	Response badRequest();

	Response unauthorized();

	Response notFound();

	Response conflict();

	String contentType();

	Response contentType(String contentType);

	String header(String name);

	Response header(String name, String value);

	Response cookie(Cookie cookie);
	
	Response cookie(String name, String value);
	
	Response cookie(String name, String value, int maxAge);
	
	Response cookie(String name, String value, int maxAge, boolean secured);
	
	Response cookie(String path, String name, String value, int maxAge, boolean secured);

	Response removeCookie(Cookie cookie);
	
	Response removeCookie(String name);

	Map<String,Object> attributes();

	Response attribute(String name, Object object);

	Response text(String output);
	
	Response html(String output);
	
	Response json(String output);
	
	Response xml(String output);
	
	ServletOutputStream outputStream() throws IOException;

	Response render(String view) ;

	Response render(ModelAndView modelAndView) ;

	void redirect(String path);
	
	void go(String path);
	
	boolean isWritten();

}
