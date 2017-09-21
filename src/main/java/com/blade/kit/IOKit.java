package com.blade.kit;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

/**
 * @author biezhi
 * 2017/6/2
 */
@Slf4j
@NoArgsConstructor
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
        BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(file));
        return bufferedReader.lines().collect(Collectors.joining());
    }

    public static String readToString(InputStream input) throws IOException {
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(input, "UTF-8"))) {
            return buffer.lines().collect(Collectors.joining("\n"));
        }
    }

    public static void copyFile(File source, File dest) throws IOException {
        FileChannel inputChannel  = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        } finally {
            if(null != inputChannel){
                inputChannel.close();
            }
            if(null != outputChannel){
                outputChannel.close();
            }
        }
    }

}
