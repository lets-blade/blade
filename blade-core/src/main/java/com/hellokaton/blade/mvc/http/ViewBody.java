package com.hellokaton.blade.mvc.http;

import com.hellokaton.blade.mvc.ui.ModelAndView;
import io.netty.handler.codec.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ViewBody implements Body {

    private final ModelAndView modelAndView;

    public ViewBody(ModelAndView modelAndView) {
        this.modelAndView = modelAndView;
    }

    public static ViewBody of(ModelAndView modelAndView){
        return new ViewBody(modelAndView);
    }

    public ModelAndView modelAndView() {
        return modelAndView;
    }

    @Override
    public HttpResponse write(BodyWriter writer) {
        return writer.onView(this);
    }

}