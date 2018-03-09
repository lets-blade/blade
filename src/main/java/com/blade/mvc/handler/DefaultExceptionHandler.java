package com.blade.mvc.handler;

import com.blade.Blade;
import com.blade.exception.BladeException;
import com.blade.exception.InternalErrorException;
import com.blade.exception.NotFoundException;
import com.blade.mvc.WebContext;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;
import com.blade.mvc.ui.HtmlCreator;
import lombok.extern.slf4j.Slf4j;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

import static com.blade.mvc.Const.*;

/**
 * Default exception handler implements
 *
 * @author biezhi
 * @date 2017/9/18
 */
@Slf4j
public class DefaultExceptionHandler implements ExceptionHandler {

    @Override
    public void handle(Exception e) {
        Response response = WebContext.response();
        Request  request  = WebContext.request();
        if (e instanceof BladeException) {
            handleBladeException((BladeException) e, request, response);
        } else {
            handleException(e, request, response);
        }
    }

    private void handleException(Exception e, Request request, Response response) {
        log.error("Handle exception => ", e);
        if (null != response) {
            response.status(500);
            request.attribute("title", "500 Internal Server Error");
            request.attribute("message", e.getMessage());
            request.attribute("stackTrace", getStackTrace(e));
            this.render500(request, response);
        }
    }

    private void handleBladeException(BladeException e, Request request, Response response) {
        Blade blade = WebContext.blade();
        response.status(e.getStatus());
        request.attribute("title", e.getStatus() + " " + e.getName());
        request.attribute("message", e.getMessage());
        if (null != e.getCause()) {
            request.attribute(VARIABLE_STACKTRACE, getStackTrace(e));
        }

        if (e.getStatus() == InternalErrorException.STATUS) {
            e.printStackTrace();
            this.render500(request, response);
        }
        if (e.getStatus() == NotFoundException.STATUS) {
            Optional<String> page404 = Optional.ofNullable(blade.environment().get(ENV_KEY_PAGE_404, null));
            if (page404.isPresent()) {
                response.render(page404.get());
            } else {
                HtmlCreator htmlCreator = new HtmlCreator();
                htmlCreator.center("<h1>404 Not Found - " + request.uri() + "</h1>");
                htmlCreator.hr();
                response.html(htmlCreator.html());
            }
        }
    }

    private void render500(Request request, Response response) {
        Blade            blade   = WebContext.blade();
        Optional<String> page500 = Optional.ofNullable(blade.environment().get(ENV_KEY_PAGE_500, null));
        if (page500.isPresent()) {
            response.render(page500.get());
        } else {
            if (blade.devMode()) {
                HtmlCreator htmlCreator = new HtmlCreator();
                htmlCreator.center("<h1>" + request.attribute("title") + "</h1>");
                htmlCreator.startP("message-header");
                htmlCreator.add("Error Message: " + request.attribute("message"));
                htmlCreator.endP();
                if (null != request.attribute(VARIABLE_STACKTRACE)) {
                    htmlCreator.startP("message-body");
                    htmlCreator.add(request.attribute(VARIABLE_STACKTRACE).toString().replace("\n", "<br/>"));
                    htmlCreator.endP();
                }
                response.html(htmlCreator.html());
            } else {
                response.html(INTERNAL_SERVER_ERROR_HTML);
            }
        }
    }

    private String getStackTrace(Throwable exception) {
        StringWriter errors = new StringWriter();
        exception.printStackTrace(new PrintWriter(errors));
        return errors.toString();
    }

}
