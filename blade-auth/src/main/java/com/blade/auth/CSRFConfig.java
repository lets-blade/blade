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
package com.blade.auth;

/**
 * CSRF Config
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class CSRFConfig {
	
	// For the global key generation token, default random string 
	String secret = "blade";
	
	// ID to save the session name of the user, default is "csrf_token" 
	String session = "csrf_token";
    
	// HTTP request header information field for passing the token, default is "X-CSRFToken"" 
	String header = "X-CSRFToken";
	
	// Form field name for passing a token, defaukt is "_csrf"
	String form = "_csrf";
	
	// Cookie name for passing a token, default is "_csrf"
	String cookie = "_csrf";
	
	// Cookie path, default is "/"
	String cookiePath = "/";
	
	// Generate the token's length, the default 32 
	int length = 32;
	
	// Cookie long, the default 60 seconds 
	int expire = 3600;
	
	// Is used to specify whether the Cookie is set to HTTPS, default is false 
	boolean secured = false;
	
	// Is used to specify whether the token is set to the header information in the response, default is false
	boolean setHeader = false;
	
	// Is used to specify whether the token is set to the Cookie of the response, default is false
	boolean setCookie = false;
	
	public CSRFConfig() {
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public void setForm(String form) {
		this.form = form;
	}

	public void setCookie(String cookie) {
		this.cookie = cookie;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public void setExpire(int expire) {
		this.expire = expire;
	}

	public void setSetHeader(boolean setHeader) {
		this.setHeader = setHeader;
	}

	public void setSetCookie(boolean setCookie) {
		this.setCookie = setCookie;
	}

	public void setCookiePath(String cookiePath) {
		this.cookiePath = cookiePath;
	}

	public void setSecured(boolean secured) {
		this.secured = secured;
	}
	
}
