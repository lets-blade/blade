/**
 * Copyright (c) 2015, biezhi 王爵 (biezhi.me@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blade.mvc.view.template;

import com.blade.Blade;
import com.blade.context.WebContextHolder;
import com.blade.kit.StreamKit;
import com.blade.mvc.view.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.*;


/**
 * JSP Render, Default Render
 *
 * @author    <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.6.6
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
            HttpServletResponse servletResponse = WebContextHolder.response().raw();
            servletResponse.setContentType("text/html;charset=utf-8");
            String realPath = new File(Blade.$().webRoot() + File.separatorChar + templatePath + File.separatorChar + modelAndView.getView()).getPath();
            String content = StreamKit.readText(new BufferedReader(new FileReader(new File(realPath))));
            servletResponse.getWriter().print(content);
        } catch (IOException e) {
            throw new TemplateException(e.getMessage());
        }
    }

}
