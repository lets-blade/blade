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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletResponse;

import com.blade.Blade;
import com.blade.context.WebApplicationContext;
import com.blade.kit.StreamKit;
import com.blade.mvc.view.ModelAndView;


/**
 * JSP Render, Default Render
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.6.6
 */
public final class DefaultEngine implements TemplateEngine {
	
	private String templatePath = "/templates/";
	
	public DefaultEngine() {
	}
	
	public DefaultEngine(String templatePath) {
		this.templatePath = templatePath;
	}
	
	@Override
	public void render(ModelAndView modelAndView, Writer writer) throws TemplateException {
		try {
			HttpServletResponse servletResponse = WebApplicationContext.response().raw();
			servletResponse.setContentType("text/html;charset=utf-8");
			String realPath = new File(Blade.$().webRoot() + File.separatorChar + templatePath + File.separatorChar + modelAndView.getView()).getPath();
			String content = StreamKit.readText(new BufferedReader(new FileReader(new File(realPath))));
			servletResponse.getWriter().print(content);
		} catch (IOException e) {
			throw new TemplateException(e);
		}
	}
    
}
