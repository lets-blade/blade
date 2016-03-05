package com.blade.route;

import java.util.Map;

import com.blade.view.ModelAndView;

public class C {
	
	public ModelAndView render(String view){
		return new ModelAndView(view);
	}
	
	public ModelAndView render(Map<String, Object> model, String view){
		return new ModelAndView(model, view);
	}
	
}