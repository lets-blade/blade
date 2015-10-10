package blade.exception;

import blade.servlet.Request;
import blade.servlet.Response;

public interface ExceptionHandler {

	void handle(Exception e, Request request, Response response);
	
}
