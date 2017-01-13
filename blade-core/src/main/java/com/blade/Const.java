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
package com.blade;

/**
 * Const Interface
 *
 * <pre>
 *     The basic configuration of the blade framework is stored
 * </pre>
 * 
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.6.6
 */
public interface Const {

	/**
	 * the last blade framework version
	 */
	String	VERSION				= "1.7.0-beta";

	/**
	 * server 500
	 */
	String VIEW_500				= "<html><head><title>500 Internal Error</title></head><body bgcolor=\"white\"><center><h1>500 Internal Error</h1></center><hr><center>blade " + VERSION +"</center></body></html>";

    /**
	 * server 404
	 */
	String VIEW_404				= "<html><head><title>404 Not Found</title></head><body bgcolor=\"white\"><center><h1>[ %s ] Not Found</h1></center><hr><center>blade " + VERSION +"</center></body></html>";

	/**
	 * server 405
	 */
	String VIEW_405				= "<html><head><title>403 Uri Forbidden</title></head><body bgcolor=\"white\"><center><h1>[ %s ] Method Not Allowed</h1></center><hr><center>blade " + VERSION +"</center></body></html>";

	/**
	 * default web server port
	 */
	int		DEFAULT_PORT		= 9000;

	String	DEFAULT_ENCODING	= "UTF-8";

	String	DEFAULT_ROUTE_CONF	= "route.conf";

	/**** package names ****/
	String	ROUTE_PKGS			= "route_packages";
	String	IOC_PKGS			= "ioc_packages";
	String	CONFIG_PKGS			= "config_packages";
	String	RESOURCE_PKGS		= "resouce_packages";
	String	BASE_PKG			= "base_package";
	String	INTERCEPTOR_PKG		= "interceptor_package";
	String	FILTER_PKG			= "filter_package";
	String	LISTENER_PKG		= "listener_package";
	
	/**** blade properties ****/
	String	BLADE_ROUTE			= "blade.route";
	String	BLADE_IOC 			= "blade.ioc";
	String	BLADE_VIEW_404 		= "blade.view404";
	String	BLADE_VIEW_500 		= "blade.view500";
	String 	BLADE_DEV 			= "blade.dev";
	String	APP_PROPERTIES		= "app.properties";
	String	SERVER_PORT			= "server.port";
	String 	JETTY_SERVER_CLASS	= "com.blade.embedd.EmbedJettyServer";
	String 	TOMCAT_SERVER_CLASS	= "com.blade.embedd.EmbedTomcatServer";
	String 	MVC_VIEW_404		= "mvc.view.404";
	String 	MVC_VIEW_500		= "mvc.view.500";
	String	HTTP_ENCODING		= "http.encoding";
	String	MVC_STATICS			= "mvc.statics";
	String 	APP_DEV				= "app.dev";
	String	APP_IOC				= "app.ioc";
	String	APP_BASE_PKG		= "app.base-package";
	String	APP_CLASSPATH		= "app.classpath";
}