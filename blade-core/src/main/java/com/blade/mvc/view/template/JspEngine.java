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
package com.blade.mvc.view.template;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.blade.context.WebContextHolder;
import com.blade.mvc.view.ModelAndView;


/**
 * JSP Render, Default Render
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.5
 */
public final class JspEngine implements TemplateEngine {
	
	private String viewPath = "/views/";
	private String suffix = ".jsp";
	
	public JspEngine() {
	}
	
	public JspEngine(String viewPath) {
		this.viewPath = viewPath;
	}
	
	public String getViewPath() {
		return viewPath;
	}

	public void setViewPath(String viewPath) {
		this.viewPath = viewPath;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	@Override
	public void render(ModelAndView modelAndView, Writer writer) throws TemplateException {
		HttpServletRequest request = WebContextHolder.request().raw();
		HttpServletResponse response = WebContextHolder.response().raw();
		try {
			Map<String, Object> model = modelAndView.getModel();
			String realPath = viewPath + modelAndView.getView() + suffix;
			
			if (!model.isEmpty()) {
				Set<String> keys = model.keySet();
				for (String key : keys) {
					request.setAttribute(key, model.get(key));
				}
			}
			request.getRequestDispatcher(realPath).forward(request, response);
		} catch (ServletException e) {
			throw new TemplateException(e);
		} catch (IOException e) {
			throw new TemplateException(e);
		} catch (Exception e) {
			throw new TemplateException(e);
		}
	}
    
}
