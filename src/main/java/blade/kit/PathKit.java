/**
 * Copyright (c) 2015, biezhi 王爵 (biezhi.me@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package blade.kit;

import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * 路径工具类，处理request上的路径
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public final class PathKit {
	
	public static final String ALL_PATHS = "+/*paths";
    private static final String SLASH_WILDCARD = "/*";
    private static final String SLASH = "/";
    private static final String FILTER_MAPPING_PARAM = "filterMappingUrlPattern";
    
    private PathKit() {
    }

    public static String getRelativePath(HttpServletRequest request, String filterPath) {
        String path = request.getRequestURI();
        String contextPath = request.getContextPath();

        path = path.substring(contextPath.length());

        if (path.length() > 0) {
            path = path.substring(1);
        }

        if (!path.startsWith(filterPath) && filterPath.equals(path + SLASH)) {
            path += SLASH;
        }
        if (path.startsWith(filterPath)) {
            path = path.substring(filterPath.length());
        }

        if (!path.startsWith(SLASH)) {
            path = SLASH + path;
        }

        try {
            path = URLDecoder.decode(path, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
        }
        return path;
    }

    public static String getFilterPath(FilterConfig config) {
        String result = config.getInitParameter(FILTER_MAPPING_PARAM);
        if (result == null || result.equals(SLASH_WILDCARD)) {
            return "";
        } else if (!result.startsWith(SLASH) || !result.endsWith(SLASH_WILDCARD)) {
            throw new RuntimeException(
                    "The " + FILTER_MAPPING_PARAM + " must start with \"/\" and end with \"/*\". It's: "
                            + result
            );
        }
        return result.substring(1, result.length() - 1);
    }
    
    public static List<String> convertRouteToList(String route) {
        String[] pathArray = route.split("/");
        List<String> path = new ArrayList<String>();
        for (String p : pathArray) {
            if (p.length() > 0) {
                path.add(p);
            }
        }
        return path;
    }
    
    public static boolean isParam(String routePart) {
        return routePart.startsWith(":");
    }

    public static boolean isSplat(String routePart) {
        return routePart.equals("*");
    }
    
}
