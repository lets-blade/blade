/**
 * 
 */
package com.blade.oauth2.issuer;

import blade.kit.EncrypKit;

public class MD5Generator extends ValueGenerator {

	@Override
	public String generateValue(String param) {
		return EncrypKit.md5(param);
	}
	
}
