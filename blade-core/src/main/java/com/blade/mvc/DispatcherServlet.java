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
package com.blade.mvc;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.blade.Blade;
import com.blade.Const;

/**
 * Blade Core DispatcherServlet
 * 
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.7.1-alpha
 */
public class DispatcherServlet extends HttpServlet {
	
	private static final long serialVersionUID = -2607425162473178733L;
	
	private Blade blade;
	private DispatcherHandler dispatcherHandler;
	
	public DispatcherServlet() {
	}
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		blade = Blade.$();
		dispatcherHandler = new DispatcherHandler(config.getServletContext(), blade.routers());
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.service(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.service(req, resp);
	}

	@Override
	protected void service(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws ServletException, IOException {
		httpRequest.setCharacterEncoding(blade.encoding());
		httpResponse.setCharacterEncoding(blade.encoding());
		httpResponse.setHeader("X-Powered-By", "Blade(" + Const.VERSION + ")");
		httpRequest.setAttribute("org.apache.catalina.ASYNC_SUPPORTED", true);

		dispatcherHandler.handle(httpRequest, httpResponse);
	}
	
	@Override
	public void destroy() {
		super.destroy();
	}
}
