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
package com.blade.render;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.blade.Blade;
import com.blade.BladeWebContext;

import blade.kit.log.Logger;

/**
 * JSP渲染引擎，默认的渲染器
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public final class JspRender extends Render {
	
	private static final Logger LOGGER = Logger.getLogger(JspRender.class);
	
	private JspRender() {
	}
	
	/**
	 * 视图渲染
	 */
	public Object render(final String view){
		try {
			HttpServletRequest servletRequest = BladeWebContext.servletRequest();
			HttpServletResponse servletResponse = BladeWebContext.servletResponse();
			
			// 设置编码
			servletRequest.setCharacterEncoding(Blade.encoding());
			servletResponse.setCharacterEncoding(Blade.encoding());
			
			// 构造jsp地址
			String realPath = disposeView(view);
			
			// 跳转到页面
			servletRequest.getRequestDispatcher(realPath).forward(servletRequest, servletResponse);
			
		} catch (ServletException e) {
			LOGGER.error(e);
		} catch (IOException e) {
			LOGGER.error(e);
		}
		return null;
	}
	
	/**
	 * ModelAndView渲染
	 */
	public Object render(ModelAndView modelAndView){
		try {
			HttpServletRequest servletRequest = BladeWebContext.servletRequest();
			HttpServletResponse servletResponse = BladeWebContext.servletResponse();
			
			String realPath = disposeView(modelAndView.getView());
			
			Map<String, Object> model = modelAndView.getModel();

			if (null != model && !model.isEmpty()) {
				Set<String> keys = model.keySet();
				for (String key : keys) {
					servletRequest.setAttribute(key, model.get(key));
				}
			}
			servletRequest.getRequestDispatcher(realPath).forward(servletRequest, servletResponse);
		} catch (ServletException e) {
			LOGGER.error(e);
		} catch (IOException e) {
			LOGGER.error(e);
		}
		return null;
	}
	
	public static JspRender single() {
        return JspRenderHolder.single;
    }
    
    private static class JspRenderHolder {
        private static final JspRender single = new JspRender();
    }
    
}
