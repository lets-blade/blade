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
 * Route loader based on ClassPath 
 *
 * @author    <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.5
 */
public class ClassPathRouteLoader extends AbstractFileRouteLoader {

    private File file;

    private InputStream inputStream;

    public ClassPathRouteLoader() {
    }

    public ClassPathRouteLoader(String filePath) {
        this(new File(filePath));
    }

    public ClassPathRouteLoader(File file) {
        this.file = file;
    }

    public ClassPathRouteLoader(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    protected InputStream getInputStream() throws Exception {
        if (null != this.inputStream) {
            return this.inputStream;
        }
        return new FileInputStream(file);
    }

    protected void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setFilePath(String filePath) {
        this.file = new File(filePath);
    }
}
