package blade.route;

import blade.servlet.Request;
import blade.servlet.Response;

public interface RouteHandler {

	public Object handler(Request request, Response response);
	
}
