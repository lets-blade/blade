package com.blade.verify;

/**
 * CSRF配置
 * @author biezhi
 *
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
