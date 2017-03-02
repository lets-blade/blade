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
package com.blade.kit;

import com.blade.Blade;
import com.blade.Const;
import com.blade.mvc.http.Response;
import com.blade.mvc.view.ViewSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import static com.blade.Blade.$;

public class DispatchKit {

    private static final Logger LOGGER = LoggerFactory.getLogger(DispatchKit.class);

    private static final boolean isWeb = !$().enableServer();

    private static final Class<?> appClass = $().bConfig().getApplicationClass();

    private static Boolean isDev = null;

    public static String getPath(Class<?> clazz) {
        URL url = clazz.getProtectionDomain().getCodeSource().getLocation();
        String filePath = null;
        try {
			filePath = new URI(url.getPath()).getPath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        if (filePath.endsWith(".jar")) {
            filePath = "jar:file:" + filePath + "!/";
            return filePath;
        }
        File file = new File(filePath);
        filePath = file.getAbsolutePath();
        return filePath;
    }

    public static String getWebRoot(ServletContext sc) {
        if (isWeb) {
            String dir = sc.getRealPath("/");
            if (dir == null) {
                try {
                    URL url = sc.getResource("/");
                    if (url != null && "file".equals(url.getProtocol())) {
            			try {
							dir = new URI(url.getFile()).getPath();
						} catch (URISyntaxException e) {
							e.printStackTrace();
						}
                    } else {
                        throw new IllegalStateException("Can't get webroot dir, url = " + url);
                    }
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                }
            }
            return dir;
        }
        return getPath(appClass);
    }

    public static void setNoCache(HttpServletResponse response) {
        // Http 1.0 header
        response.setHeader("Buffer", "false");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 1L);
        // Http 1.1 header
        response.setHeader("Cache-Control", "no-cache, no-store, max-age=0");
    }

    public static void setFileDownloadHeader(HttpServletResponse response, String fileName, String contentType) {
        if (contentType == null) {
            contentType = "application/x-download";
        }
        response.setContentType(contentType);
        // 中文文件名支持
        try {
            String encodedfileName = new String(fileName.getBytes(), "ISO8859-1");
            response.setHeader("Content-Disposition", "attachment; filename=" + encodedfileName);
        } catch (UnsupportedEncodingException e) {
        }
    }

    /**
     * Print Error Message
     *
     * @param err
     * @param code
     * @param response
     */
    public static void printError(Throwable err, int code, Response response) {
        if (null == isDev) {
            isDev = Blade.$().isDev();
        }
        try {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final PrintWriter writer = new PrintWriter(baos);

            // If the developer mode, the error output to the page
            if (isDev) {
                writer.println(String.format(HTML, err.getClass() + " : " + err.getMessage()));
                writer.println();
                err.printStackTrace(writer);
                writer.println(END);
            } else {
                if (code == 404) {
                    String view404 = ViewSettings.$().getView404();
                    if (StringKit.isNotBlank(view404)) {
                        try {
                            response.render(view404);
                        } catch (Exception e) {
                            LOGGER.error("", e);
                        }
                        return;
                    } else {
                        writer.write(err.getMessage());
                    }
                } else {
                    String view500 = ViewSettings.$().getView500();
                    if (StringKit.isNotBlank(view500)) {
                        try {
                            response.render(view500);
                        } catch (Exception e) {
                            LOGGER.error("", e);
                        }
                        return;
                    } else {
                        writer.write(Const.VIEW_500);
                    }
                }
            }
            writer.close();
            response.status(code);
            InputStream body = new ByteArrayInputStream(baos.toByteArray());
            print(body, response.writer());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void print(InputStream body, PrintWriter writer) throws IOException {
        print(IOKit.toString(body), writer);
    }

    public static void print(String content, PrintWriter writer) throws IOException {
        writer.print(content);
        writer.flush();
        writer.close();
    }

    private static final String HTML = "<!DOCTYPE html><html><head><meta charset='utf-8'><title>Blade Error Page</title>"
            + "<style type='text/css'>*{margin:0;padding:0}.info{margin:0;padding:10px;color:#000;background-color:#f8edc2;height:60px;line-height:60px;border-bottom:5px solid #761226}.isa_error{margin:0;padding:10px;font-size:14px;font-weight:bold;background-color:#e0c9db;border-bottom:1px solid #000}.version{color:green;font-size:16px;font-weight:bold;padding:10px}</style></head><body>"
            + "<div class='info'><h3>%s</h3></div><div class='isa_error'><pre>";

    private static final String END = "</pre></div><div class='version'>Blade-" + Const.VERSION
            + "（<a href='http://bladejava.com' target='_blank'>Blade Framework</a>） </div></body></html>";

}
