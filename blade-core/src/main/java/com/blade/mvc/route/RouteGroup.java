/**
 * Copyright (c) 2016, biezhi 王爵 (biezhi.me@gmail.com)
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
package com.blade.mvc.route;

import com.blade.Blade;
import com.blade.mvc.handler.RouteHandler;

/**
 * Route Group.
 *
 * @author    <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.5
 */
public class RouteGroup {

    private Blade blade;
    private String prefix;

    public RouteGroup(Blade blade, String prefix) {
        this.blade = blade;
        this.prefix = prefix;
    }

    public RouteGroup get(RouteHandler routeHandler) {
        blade.get(prefix, routeHandler);
        return this;
    }

    public RouteGroup get(String path, RouteHandler routeHandler) {
        blade.get(prefix + path, routeHandler);
        return this;
    }

    public RouteGroup post(RouteHandler routeHandler) {
        blade.post(prefix, routeHandler);
        return this;
    }

    public RouteGroup post(String path, RouteHandler routeHandler) {
        blade.post(prefix + path, routeHandler);
        return this;
    }

    public RouteGroup delete(RouteHandler routeHandler) {
        blade.delete(prefix, routeHandler);
        return this;
    }

    public RouteGroup delete(String path, RouteHandler routeHandler) {
        blade.delete(prefix + path, routeHandler);
        return this;
    }

    public RouteGroup put(RouteHandler routeHandler) {
        blade.put(prefix, routeHandler);
        return this;
    }

    public RouteGroup put(String path, RouteHandler routeHandler) {
        blade.put(prefix + path, routeHandler);
        return this;
    }

}
