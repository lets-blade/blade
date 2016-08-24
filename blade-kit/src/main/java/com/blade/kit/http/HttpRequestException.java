package com.blade.kit.http;

import java.io.IOException;

/**
 * HTTP请求异常
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class HttpRequestException extends RuntimeException {

	private static final long serialVersionUID = -1170466989781746231L;
	
	public HttpRequestException(final IOException cause) {
		super(cause);
	}
	
	@Override
	public IOException getCause() {
		return (IOException) super.getCause();
	}
}