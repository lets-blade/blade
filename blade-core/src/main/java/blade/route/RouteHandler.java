package blade.route;

import blade.servlet.Request;
import blade.servlet.Response;

public interface RouteHandler {

	public String run(Request request, Response response);
	
}
