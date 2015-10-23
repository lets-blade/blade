package com.blade.verify;

/**
 * CSRF配置
 * @author biezhi
 *
 */
public class CSRFConfig {
	
	String salt = CSRFTokenManager.CSRF_PARAM_NAME;
    
	int length = 32;
	
	public CSRFConfig() {
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public void setLength(int length) {
		this.length = length;
	}
	
}
