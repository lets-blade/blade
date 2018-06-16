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
    public <T> T write(BodyWriter<T> writer) {
        return writer.onView(this);
    }
}