package com.blade;

public interface Const {

	/**
	 * 当前最新版本
	 */
	String BLADE_VERSION = "1.5.0-alpha";
	
	/**
     * 服务器500错误时返回的HTML
     */
	String INTERNAL_ERROR = "<html><head><title>500 Internal Error</title></head><body bgcolor=\"white\"><center><h1>500 Internal Error</h1></center><hr><center>blade "
			+ BLADE_VERSION +"</center></body></html>";
    
	/**
	 * 服务器404错误HTML
	 */
	String VIEW_NOTFOUND = "<html><head><title>404 Not Found</title></head><body bgcolor=\"white\"><center><h1>[ %s ] Not Found</h1></center><hr><center>blade "
			+ BLADE_VERSION +"</center></body></html>";
	
	/**
	 * jetty默认端口
	 */
	int DEFAULT_PORT = 9000;
}
