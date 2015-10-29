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
package com.blade.verify;

/**
 * 
 * <p>
 * CSRF配置
 * </p>
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class CSRFConfig {
	
	// 用于生成令牌的全局秘钥，默认为随机字符串
	String secret = "blade";
	
	// 用于保存用户 ID 的 session 名称，默认为 "csrf_token"
	String session = "csrf_token";
    
	// 用于传递令牌的 HTTP 请求头信息字段，默认为 "X-CSRFToken"
	String header = "X-CSRFToken";
	
	// 用于传递令牌的表单字段名，默认为 "_csrf"
	String form = "_csrf";
	
	// 用于传递令牌的 Cookie 名称，默认为 "_csrf"
	String cookie = "_csrf";
	
	// Cookie 设置路径，默认为 "/"
	String cookiePath = "/";
	
	// 生成token的长度，默认32位
	int length = 32;
	
	// cookie过期时长，默认60秒
	int expire = 3600;
	
	// 用于指定是否要求只有使用 HTTPS 时才设置 Cookie，默认为 false
	boolean secured = false;
	
	// 用于指定是否将令牌设置到响应的头信息中，默认为 false
	boolean setHeader = false;
	
	// 用于指定是否将令牌设置到响应的 Cookie 中，默认为 false
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
