package com.blade.mvc.multipart;


import lombok.Data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * HTTP multipart/form-data Request
 *
 * @author <a href="mailto:hellokaton@gmail.com" target="_blank">hellokaton</a>
 * @since 1.5
 */
@Data
public class FileItem {

    /**
     * Upload file field, e.g: "file", "img"
     */
    private String name;

    /**
     * Upload file name, e.g: "hello.png"
     */
    private String fileName;

    /**
     * File temp path
     */
    private String path;

    /**
     * File Content Type
     */
    private String contentType;

    /**
     * File size, unit: byte
     */
    private long length;

    /**
     * Netty upload File mode,
     * If the file size is less than 16kb, the memory mode is used.
     * <p>
     * In this mode, the file exists as a byte array and the file object is null.
     */
    private boolean inMemory;

    private transient File file;
    private transient byte[] data;

    public String extName() {
        return fileName.substring(fileName.lastIndexOf("."));
    }

    @Override
    public String toString() {
        long kb = length / 1024;
        return "FileItem(" +
                "name='" + name + '\'' +
                ", fileName='" + fileName + '\'' +
                ", path='" + path + '\'' +
                ", contentType='" + contentType + '\'' +
                ", size=" + (kb < 1 ? 1 : kb) + "KB)";
    }

    public void moveTo(File newFile) throws IOException {
        this.moveTo(Paths.get(newFile.getPath()));
    }

    public void moveTo(Path newFile) throws IOException {
        if (null != file) {
            Path tmpPath = Paths.get(file.getPath());
            Files.move(tmpPath, newFile, StandardCopyOption.REPLACE_EXISTING);
        } else {
            Files.write(newFile, this.getData());
        }
    }

    public byte[] byteArray() throws IOException {
        if (null != this.data) {
            return this.data;
        }
        if (null == this.file) {
            return null;
        }
        Path tmpPath = Paths.get(file.getPath());
        try {
            this.data = Files.readAllBytes(file.toPath());
            return this.data;
        } finally {
            Files.delete(tmpPath);
        }
    }

}