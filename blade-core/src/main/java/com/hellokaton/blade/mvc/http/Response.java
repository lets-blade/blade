package com.hellokaton.blade.mvc.http;

import com.hellokaton.blade.exception.BladeException;
import com.hellokaton.blade.kit.JsonKit;
import com.hellokaton.blade.kit.StringKit;
import com.hellokaton.blade.mvc.HttpConst;
import com.hellokaton.blade.mvc.WebContext;
import com.hellokaton.blade.mvc.ui.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Http Response
 *
 * @author biezhi
 * 2017/5/31
 */
public interface Response {

    /**
     * Get current response http status code. e.g: 200
     *
     * @return return response status code
     */
    int statusCode();

    /**
     * Setting Response Status
     *
     * @param status status code
     * @return Return Response
     */
    Response status(int status);

    /**
     * Set current response http code 400
     *
     * @return Setting Response Status is BadRequest and Return Response
     */
    default Response badRequest() {
        return status(400);
    }

    /**
     * Set current response http code 401
     *
     * @return Setting Response Status is unauthorized and Return Response
     */
    default Response unauthorized() {
        return status(401);
    }

    /**
     * Set current response http code 404
     *
     * @return Setting Response Status is notFound and Return Response
     */
    default Response notFound() {
        return status(404);
    }

    /**
     * Setting Response ContentType
     *
     * @param contentType content type
     * @return Return Response
     */
    Response contentType(String contentType);

    /**
     * Get current response headers: contentType
     *
     * @return return response content-type
     */
    String contentType();

    /**
     * Get current response headers
     *
     * @return return response headers
     */
    Map<String, String> headers();

    /**
     * Set current response header
     *
     * @param name  Header Name
     * @param value Header Value
     * @return Return Response
     */
    Response header(String name, String value);

    /**
     * Get current response cookies
     *
     * @return return response cookies
     */
    Map<String, String> cookies();

    Set<io.netty.handler.codec.http.cookie.Cookie> cookiesRaw();

    /**
     * add raw response cookie
     *
     * @param cookie
     * @return return Response instance
     */
    Response cookie(Cookie cookie);

    /**
     * Add Cookie
     *
     * @param name  Cookie Name
     * @param value Cookie Value
     * @return Return Response
     */
    Response cookie(String name, String value);

    /**
     * Setting Cookie
     *
     * @param name   Cookie Name
     * @param value  Cookie Value
     * @param maxAge Period of validity
     * @return Return Response
     */
    Response cookie(String name, String value, int maxAge);

    /**
     * Setting Cookie
     *
     * @param name    Cookie Name
     * @param value   Cookie Value
     * @param maxAge  Period of validity
     * @param secured Is SSL
     * @return Return Response
     */
    Response cookie(String name, String value, int maxAge, boolean secured);

    /**
     * Setting Cookie
     *
     * @param path    Cookie Domain Path
     * @param name    Cookie Name
     * @param value   Cookie Value
     * @param maxAge  Period of validity
     * @param secured Is SSL
     * @return Return Response
     */
    Response cookie(String path, String name, String value, int maxAge, boolean secured);

    /**
     * remove cookie
     *
     * @param name
     * @return return Response instance
     */
    Response removeCookie(String name);

    /**
     * Render by text
     *
     * @param text text content
     */
    default void text(String text) {
        if (null == text) return;
        if (null == contentType()) {
            this.contentType(HttpConst.CONTENT_TYPE_TEXT);
        }
        this.body(text);
    }

    /**
     * Render by html
     *
     * @param html html content
     */
    default void html(String html) {
        if (null == html) return;
        if (null == contentType()) {
            this.contentType(HttpConst.CONTENT_TYPE_HTML);
        }
        this.body(html);
    }

    /**
     * Render by json
     *
     * @param json json content
     */
    default void json(String json) {
        if (null == json) return;
        if (null == contentType()) {
            if (Objects.requireNonNull(WebContext.request()).isIE()) {
                this.contentType(HttpConst.CONTENT_TYPE_HTML);
            } else {
                this.contentType(HttpConst.CONTENT_TYPE_JSON);
            }
        }
        this.body(json);
    }

    /**
     * Render by json
     *
     * @param bean bean instance
     */
    default void json(Object bean) {
        if (null == bean) return;
        this.json(JsonKit.toString(bean));
    }

    /**
     * Send a string response to the client
     *
     * @param body string content
     */
    default void body(String body) {
        this.body(new StringBody(body));
    }

    /**
     * Send body to client
     *
     * @param body {@link Body}
     */
    Response body(Body body);

    /**
     * Render view, can be modified after WebHook
     *
     * @param view view page
     * @return Return Response
     */
    default void render(String view) {
        if (StringKit.isEmpty(view)) {
            throw new BladeException(500, "Render view not empty.");
        }
        this.render(new ModelAndView(view));
    }

    /**
     * Render view And Setting Data, can be modified after WebHook
     *
     * @param modelAndView ModelAndView object
     * @return Return Response
     */
    void render(ModelAndView modelAndView);

    /**
     * Redirect to newUri
     *
     * @param newUri new url
     */
    void redirect(String newUri);

    void toStatic(StaticFileBody body);

    void write(File file) throws IOException;

    void write(String fileName, File file) throws IOException;

    /**
     * @return Returns the currently set view, returning an empty Optional type when not set
     * @since 2.0.8
     */
    ModelAndView modelAndView();

    Body body();

}
