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
package com.blade.mvc.route.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * The file system based on Routing 
 *
 * @author    <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.5
 */
public class FileSystemRouteLoader extends AbstractFileRouteLoader {

    private File file;

    public FileSystemRouteLoader() {
    }

    public FileSystemRouteLoader(String filePath) {
        this(new File(filePath));
    }

    public FileSystemRouteLoader(File file) {
        this.file = file;
    }

    @Override
    protected InputStream getInputStream() throws Exception {
        return new FileInputStream(file);
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setFilePath(String filePath) {
        this.file = new File(filePath);
    }
}
