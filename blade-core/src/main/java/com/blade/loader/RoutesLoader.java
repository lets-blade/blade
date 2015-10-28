package com.blade.loader;

import java.text.ParseException;
import java.util.List;

import com.blade.route.Route;
import com.blade.route.RoutesException;

public interface RoutesLoader {

	List<Route> load() throws ParseException, RoutesException;

}
