package com.blade;

import com.blade.route.RouteHandler;
import com.blade.servlet.Request;
import com.blade.servlet.Response;

public class App extends Bootstrap {
	
	@Override
	public void init() {}
	
	public static void main(String[] args) throws Exception {
		Blade blade = Blade.me();
		blade.get("/").run(new RouteHandler() {
			@Override
			public Object handler(Request request, Response response) {
				response.html("<h1>Hello blade!</h1>");
				return null;
			}
		});
		
		blade.app(App.class).listen(9001).start();
		
	}

}
