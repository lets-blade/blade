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
package com.blade.mvc.multipart;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * HTTP multipart/form-data Request
 *
 * @author    <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.5
 */
public class FileItem {

    private String name;

    private String fileName;

    private String contentType;

    private long contentLength;

    private File file;

    private Map<String, String> headers;

    public FileItem(String fieldName, String fileName, String contentType, long contentLength, File file, Map<String, String> headers) {

        this.fileName = fileName;
        this.contentType = contentType;
        this.contentLength = contentLength;
        this.file = file;
        this.headers = headers;
        if (headers == null) {
            this.headers = new HashMap<String, String>();
        }
    }


    public String name() {
        return name;
    }

    public String fileName() {
        return fileName;
    }

    public String contentType() {
        return contentType;
    }

    public long contentLength() {
        return contentLength;
    }

    public File file() {
        return file;
    }

    public Map<String, String> headers() {
        return headers;
    }

}