package com.xxx;

import com.blade.Blade;
import com.blade.Bootstrap;

public class App extends Bootstrap {

	@Override
	public void init() {
		Blade blade = Blade.me();
		blade.routeConf("com.xxx.route", "route.conf");
		blade.config("blade.conf");
	}
	
}
