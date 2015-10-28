package com.blade.servlet.multipart;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * HTTP multipart/form-data 请求
 * @author biezhi
 *
 */
public class FileItem {

	private String name;

	private String fileName;

	private String contentType;

	private long contentLength;

	private File file;

	private Map<String,String> headers;

	public FileItem(String fieldName, String fileName, String contentType, long contentLength, File file, Map<String,String> headers) {
		
		this.fileName = fileName;
		this.contentType = contentType;
		this.contentLength = contentLength;
		this.file = file;
		this.headers = headers;
		if (headers == null) {
			this.headers = new HashMap<String,String>();
		}
	}

	public String getName() {
		return name;
	}

	public String getFileName() {
		return fileName;
	}

	public String getContentType() {
		return contentType;
	}

	public long getContentLength() {
		return contentLength;
	}

	public File getFile() {
		return file;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

}
