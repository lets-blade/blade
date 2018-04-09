package com.blade.kit;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class IOKit {

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
