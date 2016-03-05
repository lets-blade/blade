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
package com.blade.view.template;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.blade.context.BladeWebContext;
import com.blade.view.ModelAndView;

import blade.kit.logging.Logger;
import blade.kit.logging.LoggerFactory;

/**
 * JSP Render, Default Render
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public final class JspEngine implements TemplateEngine {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(JspEngine.class);
	
	private String viewPath = "/WEB-INF/";
	
	public JspEngine() {
	}
	
	public JspEngine(String viewPath) {
		this.viewPath = viewPath;
	}
	
	@Override
	public void render(ModelAndView modelAndView, Writer writer) {
		HttpServletRequest servletRequest = BladeWebContext.request().raw();
		HttpServletResponse servletResponse = BladeWebContext.response().raw();
		
		try {
			Map<String, Object> model = modelAndView.getModel();
			String realPath = viewPath + modelAndView.getView();
			
			if (null != model && !model.isEmpty()) {
				Set<String> keys = model.keySet();
				for (String key : keys) {
					servletRequest.setAttribute(key, model.get(key));
				}
			}
			servletRequest.getRequestDispatcher(realPath).forward(servletRequest, servletResponse);
		} catch (ServletException e) {
			e.printStackTrace();
			LOGGER.error("", e);
		} catch (IOException e) {
			e.printStackTrace();
			LOGGER.error("", e);
		}
	}
    
}
