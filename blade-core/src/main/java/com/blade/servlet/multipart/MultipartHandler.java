package com.blade.servlet.multipart;

public interface MultipartHandler {

	void handleFormItem(String name, String value);
	
	void handleFileItem(String name, FileItem fileItem);

}
