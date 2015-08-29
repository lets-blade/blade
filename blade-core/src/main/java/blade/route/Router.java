package blade.route;

import blade.servlet.Request;
import blade.servlet.Response;

public interface Router {

	public Object handler(Request request, Response response);
	
}
