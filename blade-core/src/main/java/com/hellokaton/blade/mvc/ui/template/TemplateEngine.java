package com.hellokaton.blade.mvc.ui.template;

import com.hellokaton.blade.exception.TemplateException;
import com.hellokaton.blade.mvc.ui.ModelAndView;

import java.io.Writer;

/**
 * TemplateEngine Interface, For view layer to display data
 *
 * @author <a href="mailto:hellokaton@gmail.com" target="_blank">hellokaton</a>
 * @since 1.5
 */
public interface TemplateEngine {

    /**
     * Render a template file to the client
     *
     * @param modelAndView ModelAndView instance, contains view name and data model
     * @param writer       writer instance
     * @throws TemplateException throw TemplateException when rendering a template
     */
    void render(ModelAndView modelAndView, Writer writer) throws TemplateException;

}