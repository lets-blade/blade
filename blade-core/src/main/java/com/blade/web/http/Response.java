/**
 * Copyright (c) 2015, biezhi 王爵 (biezhi.me@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blade.web.http;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import com.blade.render.ModelAndView;

/**
 * 
 * <p>
 * HTTP响应对象
 * </p>
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
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
	
	OutputStream outputStream() throws IOException;

	Response render(String view) ;

	Response render(ModelAndView modelAndView) ;

	void redirect(String path);
	
	void go(String path);
	
	boolean isWritten();

}
