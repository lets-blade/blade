/**
 * Copyright (c) 2015, biezhi 王爵 (biezhi.me@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blade.mvc.http;

import com.blade.mvc.view.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * HTTP Response
 *
 * @author    <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.5
 */
public interface Response {

    /**
     * @return Return HttpServletResponse
     */
    HttpServletResponse raw();

    /**
     * @return Return HTTP Status
     */
    int status();

    /**
     * Setting Response Status
     *
     * @param status status code
     * @return Return Response
     */
    Response status(int status);

    /**
     * @return Setting Response Status is BadRequest and Return Response
     */
    Response badRequest();

    /**
     * @return Setting Response Status is unauthorized and Return Response
     */
    Response unauthorized();

    /**
     * @return Setting Response Status is notFound and Return Response
     */
    Response notFound();

    /**
     * @return Setting Response Status is conflict and Return Response
     */
    Response conflict();

    /**
     * @return Return Response contentType
     */
    String contentType();

    /**
     * Setting Response ContentType
     *
     * @param contentType content type
     * @return Return Response
     */
    Response contentType(String contentType);

    /**
     * Get header
     *
     * @param name Header Name
     * @return Return Response
     */
    String header(String name);

    /**
     * Setting header
     *
     * @param name    Header Name
     * @param value    Header Value
     * @return Return Response
     */
    Response header(String name, String value);

    /**
     * Setting Cookie
     *
     * @param cookie    Cookie Object
     * @return Return Response
     */
    Response cookie(Cookie cookie);

    /**
     * Setting Cookie
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
     * Remove Cookie
     *
     * @param cookie    Cookie Object
     * @return Return Response
     */
    Response removeCookie(Cookie cookie);

    /**
     * Rmove Cookie By Name
     *
     * @param name Cookie Name
     * @return Return Response
     */
    Response removeCookie(String name);

    /**
     * Render by text
     *
     * @param text        text content
     * @return Return Response
     */
    Response text(String text);

    /**
     * Render by html
     *
     * @param html html content
     * @return Return Response
     */
    Response html(String html);

    /**
     * Render by json
     *
     * @param json json content
     * @return Return Response
     */
    Response json(String json);

    /**
     * Render by json
     *
     * @param bean
     * @return
     */
    Response json(Object bean);

    /**
     * Render by xml
     *
     * @param xml xml content
     * @return Return Response
     */
    Response xml(String xml);

    /**
     * @return Return OutputStream
     * @throws IOException IOException
     */
    OutputStream outputStream() throws IOException;

    /**
     * @return Return ResponseWriter Stream
     * @throws IOException
     */
    PrintWriter writer() throws IOException;

    /**
     * Render view
     *
     * @param view view page
     * @return Return Response
     */
    Response render(String view);

    /**
     * Render view And Setting Data
     *
     * @param modelAndView    ModelAndView object
     * @return Return Response
     */
    Response render(ModelAndView modelAndView);

    /**
     * Redirect to Path
     *
     * @param path location path
     */
    void redirect(String path);

    /**
     * Go to Path, Under contextpath
     *
     * @param path redirect location
     */
    void go(String path);

    /**
     * @return Return Response is Write
     */
    boolean isWritten();

}
