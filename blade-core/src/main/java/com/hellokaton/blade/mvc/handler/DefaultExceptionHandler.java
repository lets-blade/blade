package com.hellokaton.blade.mvc.handler;

import com.hellokaton.blade.exception.*;
import com.hellokaton.blade.kit.BladeCache;
import com.hellokaton.blade.mvc.WebContext;
import com.hellokaton.blade.mvc.http.RawBody;
import com.hellokaton.blade.mvc.http.Request;
import com.hellokaton.blade.mvc.http.Response;
import com.hellokaton.blade.mvc.ui.HtmlCreator;
import com.hellokaton.blade.mvc.ui.ModelAndView;
import com.hellokaton.blade.mvc.ui.RestResponse;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import lombok.extern.slf4j.Slf4j;
import lombok.var;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static com.hellokaton.blade.kit.BladeKit.log404;
import static com.hellokaton.blade.kit.BladeKit.log405;
import static com.hellokaton.blade.mvc.BladeConst.*;

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
        if (!ExceptionHandler.isResetByPeer(e)) {
            var response = WebContext.response();
            var request = WebContext.request();

            if (e instanceof BladeException) {
                this.handleBladeException((BladeException) e, request, response);
            } else if (e instanceof ValidatorException) {
                this.handleValidators(ValidatorException.class.cast(e), request, response);
            } else {
                this.handleException(e, request, response);
            }
        }
    }

    protected void handleValidators(ValidatorException validatorException, Request request, Response response) {
        var code = Optional.ofNullable(validatorException.getCode()).orElse(500);
        if (request.isAjax() || request.contentType().toLowerCase().contains("json")) {
            response.json(RestResponse.fail(code, validatorException.getMessage()));
        } else {
            this.handleException(validatorException, request, response);
        }
    }

    protected void handleException(Exception e, Request request, Response response) {
        log.error("", e);
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
        var blade = WebContext.blade();
        response.status(e.getStatus());

        var modelAndView = new ModelAndView();
        modelAndView.add("title", e.getStatus() + " " + e.getName());
        modelAndView.add("message", e.getMessage());

        if (null != e.getCause()) {
            request.attribute(VARIABLE_STACKTRACE, getStackTrace(e));
        }

        if (e.getStatus() == InternalErrorException.STATUS) {
            log.error("", e);
            this.render500(request, response);
        }

        String paddingMethod = BladeCache.getPaddingMethod(request.method());

        if (e.getStatus() == NotFoundException.STATUS) {
            log404(log, paddingMethod, request.uri());

            if (request.isJsonRequest()) {
                response.json(RestResponse.fail(NotFoundException.STATUS, "Not Found [" + request.uri() + "]"));
            } else {
                var page404 = Optional.ofNullable(blade.environment().get(ENV_KEY_PAGE_404, null));
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

        if (e.getStatus() == MethodNotAllowedException.STATUS) {

            log405(log, paddingMethod, request.uri());

            if (request.isJsonRequest()) {
                response.json(RestResponse.fail(MethodNotAllowedException.STATUS, e.getMessage()));
            } else {
                response.text(e.getMessage());
            }
        }
    }

    protected void render500(Request request, Response response) {
        var blade = WebContext.blade();
        var page500 = Optional.ofNullable(blade.environment().get(ENV_KEY_PAGE_500, null));

        if (page500.isPresent()) {
            this.renderPage(response, new ModelAndView(page500.get()));
        } else {
            if (blade.devMode()) {
                var htmlCreator = new HtmlCreator();
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
        var sw = new StringWriter();
        try {
            WebContext.blade().templateEngine().render(modelAndView, sw);
            ByteBuf buffer = Unpooled.wrappedBuffer(sw.toString().getBytes(StandardCharsets.UTF_8));
            FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.valueOf(response.statusCode()), buffer);
            response.body(new RawBody(fullHttpResponse));
        } catch (Exception e) {
            log.error("Render view error", e);
        }
    }

    protected String getStackTrace(Throwable exception) {
        var errors = new StringWriter();
        exception.printStackTrace(new PrintWriter(errors));
        return errors.toString();
    }

}
