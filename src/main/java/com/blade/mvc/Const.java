/**
 * Copyright (c) 2017, biezhi 王爵 (biezhi.me@gmail.com)
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
package com.blade.mvc;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Blade Const
 *
 * @author biezhi
 * 2017/6/2
 */
public interface Const {

    int          DEFAULT_SERVER_PORT        = 9000;
    String       DEFAULT_SERVER_ADDRESS     = "0.0.0.0";
    String       LOCAL_IP_ADDRESS           = "127.0.0.1";
    String       VERSION                    = "2.0.3-alpha";
    String       WEB_JARS                   = "/webjars/";
    String       CLASSPATH                  = new File(Const.class.getResource("/").getPath()).getPath();
    String       CONTENT_TYPE_HTML          = "text/html; charset=UTF-8";
    String       CONTENT_TYPE_JSON          = "application/json; charset=UTF-8";
    String       CONTENT_TYPE_TEXT          = "text/plain; charset=UTF-8";
    String       X_POWER_BY                 = "X-Powered-By";
    String       HEADER_SERVER              = "Server";
    String       HTTP_DATE_FORMAT           = "EEE, dd MMM yyyy HH:mm:ss zzz";
    String       INTERNAL_SERVER_ERROR_HTML = "<center><h1>500 Internal Server Error</h1><hr/></center>";
    List<String> PLUGIN_PACKAGE_NAME        = Arrays.asList("com.blade.plugin");
    List<String> DEFAULT_STATICS            = Arrays.asList("/favicon.ico", "/robots.txt", "/static/", "/upload/", "/webjars/");

    // Env key
    String ENV_KEY_DEV_MODE                = "app.devMode";
    String ENV_KEY_APP_NAME                = "app.name";
    String ENV_KEY_GZIP_ENABLE             = "http.gzip.enable";
    String ENV_KEY_CORS_ENABLE             = "http.cors.enable";
    String ENV_KEY_SESSION_KEY             = "http.session.key";
    String ENV_KEY_SESSION_TIMEOUT         = "http.session.timeout";
    String ENV_KEY_AUTH_USERNAME           = "http.auth.username";
    String ENV_KEY_AUTH_PASSWORD           = "http.auth.password";
    String ENV_KEY_PAGE_404                = "mvc.view.404";
    String ENV_KEY_PAGE_500                = "mvc.view.500";
    String ENV_KEY_STATIC_DIRS             = "mvc.statics";
    String ENV_KEY_STATIC_LIST             = "mvc.statics.show-list";
    String ENV_KEY_TEMPLATE_PATH           = "mvc.template.path";
    String ENV_KEY_SERVER_ADDRESS          = "server.address";
    String ENV_KEY_SERVER_PORT             = "server.port";
    String ENV_KEY_NETTY_BOOS_GROUP_NAME   = "server.netty.boos-name";
    String ENV_KEY_NETTY_WORKER_GROUP_NAME = "server.netty.worker-name";
    String ENV_KEY_NETTY_THREAD_COUNT      = "server.netty.thread-count";
    String ENV_KEY_NETTY_WORKERS           = "server.netty.workers";
    String ENV_KEY_NETTY_SO_BACKLOG        = "server.netty.backlog";
    String ENV_KEY_NETTY_CONN_TIMEOUT      = "server.netty.child.conn-timeout";
    String ENV_KEY_NETTY_REECEIVE_BUF      = "server.netty.child.receive-buf";
    String ENV_KEY_NETTY_SEND_BUF          = "server.netty.child.send-buf";
    String ENV_KEY_NETTY_CHILD_TCP_NODELAY = "server.netty.child.tcp-nodelay";
    String ENV_KEY_NETTY_CHILD_KEEPALIVE   = "server.netty.child.keep-alive";
    String ENV_KEY_NETTY_CHILD_LINGER      = "server.netty.child.linger";
    String ENV_KEY_BOOT_CONF               = "boot_conf";

    // terminal
    String TERMINAL_SERVER_ADDRESS = "--server.address=";
    String TERMINAL_SERVER_PORT    = "--server.port=";
    String TERMINAL_BLADE_ENV      = "--blade.env=";

    String[] BANNER_TEXT = {
            " __, _,   _, __, __,",
            " |_) |   /_\\ | \\ |_",
            " |_) | , | | |_/ |",
            " ~   ~~~ ~ ~ ~   ~~~"
    };

}
