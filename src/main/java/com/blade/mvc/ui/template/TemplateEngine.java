package com.blade.mvc.ui.template;

import com.blade.exception.TemplateException;
import com.blade.mvc.ui.ModelAndView;

import java.io.Writer;

/**
 * TemplateEngine Interface, For view layer to display data
 *
 * @author <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.5
 */
public interface TemplateEngine {

    void render(ModelAndView modelAndView, Writer writer) throws TemplateException;

}