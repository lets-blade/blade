package com.blade.mvc.multipart;


import com.blade.kit.json.JsonIgnore;
import lombok.Data;

/**
 * HTTP multipart/form-data Request
 *
 * @author <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.5
 */
@Data
public class FileItem {

    private String name;
    private String fileName;
    private String contentType;
    private long   length;
    @JsonIgnore
    private byte[] data;

    public FileItem(String name, String fileName, String contentType, long length) {
        this.name = name;
        this.fileName = fileName;
        this.contentType = contentType;
        this.length = length;
    }

    @Override
    public String toString() {
        long kb = length / 1024;
        return "FileItem(" +
                "name='" + name + '\'' +
                ", fileName='" + fileName + '\'' +
                ", contentType='" + contentType + '\'' +
                ", size=" + (kb < 1 ? 1 : kb) + "KB)";
    }
}