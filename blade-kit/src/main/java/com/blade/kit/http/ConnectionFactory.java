package com.blade.kit.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;

/**
 * HttpURLConnection工厂
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public interface ConnectionFactory {
	/**
	 * 根据URL创建一个HttpURLConnection
	 *
	 * @throws IOException
	 */
	HttpURLConnection create(URL url) throws IOException;

	/**
	 * 根据URL和代理对象创建一个HttpURLConnection
	 *
	 * @throws IOException
	 */
	HttpURLConnection create(URL url, Proxy proxy) throws IOException;

	/**
	 * 一个默认的连接工厂
	 */
	ConnectionFactory DEFAULT = new ConnectionFactory() {
		public HttpURLConnection create(URL url) throws IOException {
			return (HttpURLConnection) url.openConnection();
		}

		public HttpURLConnection create(URL url, Proxy proxy)
				throws IOException {
			return (HttpURLConnection) url.openConnection(proxy);
		}
	};
}