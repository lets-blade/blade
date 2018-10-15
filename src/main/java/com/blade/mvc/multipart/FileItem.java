package com.blade.mvc.multipart;


import com.blade.exception.BladeException;
import com.blade.kit.json.JsonIgnore;
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
 * @author <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
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

    @JsonIgnore
    private File file;

    public String extName() {
        return fileName.substring(fileName.lastIndexOf("."));
    }

    @Override
    public String toString() {
        long kb = length / 1024;
        return "FileItem(" +
                "name='" + name + '\'' +
                "fileName='" + fileName + '\'' +
                ", path='" + path + '\'' +
                ", contentType='" + contentType + '\'' +
                ", size=" + (kb < 1 ? 1 : kb) + "KB)";
    }

    public void moveTo(File newFile) throws IOException {
        this.moveTo(Paths.get(newFile.getPath()));
    }

    public void moveTo(Path newFile) throws IOException {
        Files.move(Paths.get(file.getPath()), newFile, StandardCopyOption.REPLACE_EXISTING);
    }

    public byte[] getData() {
        byte[] fileContent;
        try {
            fileContent = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return fileContent;
    }

}