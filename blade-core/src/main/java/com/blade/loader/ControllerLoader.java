package com.blade.loader;

import com.blade.route.RoutesException;

public interface ControllerLoader {
	
	Object load(String controllerName) throws RoutesException;
	
}
