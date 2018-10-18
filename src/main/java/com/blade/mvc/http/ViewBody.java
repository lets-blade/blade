package com.blade.mvc.http;

import com.blade.mvc.ui.ModelAndView;
import io.netty.handler.codec.http.FullHttpResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ViewBody implements Body {

    private final ModelAndView modelAndView;

    public ViewBody(ModelAndView modelAndView) {
        this.modelAndView = modelAndView;
    }

    public ModelAndView modelAndView() {
        return modelAndView;
    }

    @Override
    public FullHttpResponse write(BodyWriter writer) {
        return writer.onView(this);
    }

}