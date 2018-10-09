package com.blade.mvc.http;

import com.blade.mvc.ui.ModelAndView;

public class ViewBody implements Body {

    private final ModelAndView modelAndView;

    public ViewBody(ModelAndView modelAndView) {
        this.modelAndView = modelAndView;
    }

    public ModelAndView modelAndView() {
        return modelAndView;
    }

    @Override
    public void write(BodyWriter writer) {
        writer.onView(this);
    }
}