package com.blade.render;

import java.io.Writer;

/**
 * 渲染器接口
 * @author biezhi
 *
 */
public interface Render {
	
	public void render(ModelAndView modelAndView, Writer writer) throws RenderException;
	
}
