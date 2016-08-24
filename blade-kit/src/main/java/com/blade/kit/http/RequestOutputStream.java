package com.blade.kit.http;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

/**
 * 请求输出流包装
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class RequestOutputStream extends BufferedOutputStream {

	final CharsetEncoder encoder;

	/**
	 * Create request output stream
	 *
	 * @param stream
	 * @param charset
	 * @param bufferSize
	 */
	public RequestOutputStream(final OutputStream stream, final String charset, final int bufferSize) {
		super(stream, bufferSize);

		encoder = Charset.forName(HttpRequest.getValidCharset(charset)).newEncoder();
	}

	/**
	 * Write string to stream
	 *
	 * @param value
	 * @return this stream
	 * @throws IOException
	 */
	public RequestOutputStream write(final String value) throws IOException {
		final ByteBuffer bytes = encoder.encode(CharBuffer.wrap(value));

		super.write(bytes.array(), 0, bytes.limit());

		return this;
	}
}