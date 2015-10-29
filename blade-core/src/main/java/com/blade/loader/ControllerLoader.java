package com.blade.loader;

import com.blade.route.RoutesException;

/**
 * 
 * <p>
 * 控制器加载接口
 * </p>
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public interface ControllerLoader {
	
	Object load(String controllerName) throws RoutesException;
	
}
