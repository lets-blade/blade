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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * 路径工具类，处理request上的路径
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public final class PathKit {
	
	public static final String ALL_PATHS = "/*";
    private static final String SLASH = "/";
    
    private PathKit() {
    }

    public static String getRelativePath(HttpServletRequest request) {
    	
        String path = request.getRequestURI();
        String contextPath = request.getContextPath();

        path = path.substring(contextPath.length());

        if (path.length() > 0) {
            path = path.substring(1);
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
