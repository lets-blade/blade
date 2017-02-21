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

/**
 * HTTP Status
 *
 * @author    <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.5
 */
public interface HttpStatus {

    int OK = 200;
    int CREATED = 201;
    int ACCEPTED = 202;
    int PARTIAL_INFO = 203;
    int NO_RESPONSE = 204;
    int MOVED = 301;
    int FOUND = 302;
    int METHOD = 303;
    int NOT_MODIFIED = 304;
    int BAD_REQUEST = 400;
    int UNAUTHORIZED = 401;
    int PAYMENT_REQUIRED = 402;
    int FORBIDDEN = 403;
    int NOT_FOUND = 404;
    int METHOD_NOT_ALLOWED = 405;
    int CONFLICT = 409;
    int INTERNAL_ERROR = 500;
    int NOT_IMPLEMENTED = 501;
    int OVERLOADED = 502;
    int GATEWAY_TIMEOUT = 503;

}
