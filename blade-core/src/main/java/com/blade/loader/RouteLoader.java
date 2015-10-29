package com.blade.loader;

import java.text.ParseException;
import java.util.List;

import com.blade.route.Route;
import com.blade.route.RouteException;

/**
 * 
 * <p>
 * 路由加载器
 * </p>
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public interface RouteLoader {

	List<Route> load() throws ParseException, RouteException;

}
