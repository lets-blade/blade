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
package com.blade.kit;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

/**
 * IO Kit
 * 
 * @author biezhi
 * 2017/6/2
 */
@Slf4j
@UtilityClass
public class IOKit {

    public static void closeQuietly(Closeable closeable) {
        try {
            if (null == closeable) {
                return;
            }
            closeable.close();
        } catch (Exception e) {
            log.error("Close closeable error", e);
        }
    }

    public static String readToString(String file) throws IOException {
        return readToString(Paths.get(file));
    }

    public static String readToString(BufferedReader bufferedReader) {
        return bufferedReader.lines().collect(Collectors.joining());
    }

    public static String readToString(Path path) throws IOException {
        BufferedReader bufferedReader = Files.newBufferedReader(path);
        return bufferedReader.lines().collect(Collectors.joining());
    }

    public static String readToString(InputStream input) throws IOException {
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(input, "UTF-8"))) {
            return buffer.lines().collect(Collectors.joining("\n"));
        }
    }
    
    public static void copyFile(File source, File dest) throws IOException {
        try (FileChannel in = new FileInputStream(source).getChannel(); FileChannel out = new FileOutputStream(dest).getChannel();){
            out.transferFrom(in, 0, in.size());
        }
    }

}
