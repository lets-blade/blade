package com.blade.mvc.multipart;


import com.blade.kit.json.JsonIgnore;

/**
 * HTTP multipart/form-data Request
 *
 * @author <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.5
 */
public class FileItem {

    private String name;
    private String fileName;
    private String contentType;
    private long length;

    @JsonIgnore
    private byte[] data;

    public FileItem(String name, String fileName, String contentType, long length) {
        this.name = name;
        this.fileName = fileName;
        this.contentType = contentType;
        this.length = length;
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

    public long length() {
        return length;
    }

    public byte[] data() {
        return data;
    }

    public void data(byte[] data) {
        this.data = data;
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