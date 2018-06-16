package com.blade.mvc.handler;

import com.blade.Blade;
import com.blade.exception.BladeException;
import com.blade.exception.InternalErrorException;
import com.blade.exception.NotFoundException;
import com.blade.exception.ValidatorException;
import com.blade.mvc.WebContext;
import com.blade.mvc.http.RawBody;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;
import com.blade.mvc.ui.HtmlCreator;
import com.blade.mvc.ui.ModelAndView;
import com.blade.mvc.ui.RestResponse;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
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
        if (!isResetByPeer(e)) {
            Response response = WebContext.response();
            Request  request  = WebContext.request();

            if (e instanceof BladeException) {
                this.handleBladeException((BladeException) e, request, response);
            } else if (ValidatorException.class.isInstance(e)) {
                this.handleValidators(ValidatorException.class.cast(e), request, response);
            } else {
                this.handleException(e, request, response);
            }
        }
    }

    protected void handleValidators(ValidatorException validatorException, Request request, Response response) {
        Integer code = Optional.ofNullable(validatorException.getCode()).orElse(500);
        if (request.isAjax() || request.contentType().toLowerCase().contains("json")) {
            response.json(RestResponse.fail(code, validatorException.getMessage()));
        } else {
            this.handleException(validatorException, request, response);
        }
    }

    protected void handleException(Exception e, Request request, Response response) {
        log.error("Request Exception", e);
        if (null == response) {
            return;
        }
        response.status(500);
        request.attribute("title", "500 Internal Server Error");
        request.attribute("message", e.getMessage());
        request.attribute("stackTrace", getStackTrace(e));
        this.render500(request, response);
    }

    protected void handleBladeException(BladeException e, Request request, Response response) {
        Blade blade = WebContext.blade();
        response.status(e.getStatus());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.add("title", e.getStatus() + " " + e.getName());
        modelAndView.add("message", e.getMessage());

        if (null != e.getCause()) {
            request.attribute(VARIABLE_STACKTRACE, getStackTrace(e));
        }

        if (e.getStatus() == InternalErrorException.STATUS) {
            log.error("Request Exception", e);
            this.render500(request, response);
        }

        if (e.getStatus() == NotFoundException.STATUS) {
            Optional<String> page404 = Optional.ofNullable(blade.environment().get(ENV_KEY_PAGE_404, null));
            if (page404.isPresent()) {
                modelAndView.setView(page404.get());
                renderPage(response, modelAndView);
                response.render(page404.get());
            } else {
                HtmlCreator htmlCreator = new HtmlCreator();
                htmlCreator.center("<h1>404 Not Found - " + request.uri() + "</h1>");
                htmlCreator.hr();
                response.html(htmlCreator.html());
            }
        }
    }

    protected void render500(Request request, Response response) {
        Blade            blade   = WebContext.blade();
        Optional<String> page500 = Optional.ofNullable(blade.environment().get(ENV_KEY_PAGE_500, null));

        if (page500.isPresent()) {
            renderPage(response, new ModelAndView(page500.get()));
        } else {
            if (blade.devMode()) {
                HtmlCreator htmlCreator = new HtmlCreator();
                htmlCreator.center("<h1>" + request.attribute("title") + "</h1>");
                htmlCreator.startP("message-header");
                htmlCreator.add("Request URI: " + request.uri());
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

    protected void renderPage(Response response, ModelAndView modelAndView) {
        StringWriter sw = new StringWriter();
        try {
            WebContext.blade().templateEngine().render(modelAndView, sw);
            ByteBuf          buffer           = Unpooled.wrappedBuffer(sw.toString().getBytes("utf-8"));
            FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.valueOf(response.statusCode()), buffer);
            response.body(new RawBody(fullHttpResponse));
        } catch (Exception e) {
            log.error("Render view error", e);
        }
    }

    protected String getStackTrace(Throwable exception) {
        StringWriter errors = new StringWriter();
        exception.printStackTrace(new PrintWriter(errors));
        return errors.toString();
    }

    private boolean isResetByPeer(Exception e) {
        if (IOException.class.isInstance(e) && "Connection reset by peer".equals(e.getMessage())) {
            return true;
        }
        return false;
    }
}
