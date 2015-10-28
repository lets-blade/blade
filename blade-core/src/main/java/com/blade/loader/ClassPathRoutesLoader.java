package com.blade.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class ClassPathRoutesLoader extends AbstractFileRoutesLoader {

	private File file;

	private InputStream inputStream;
	
	public ClassPathRoutesLoader() {
	}

	public ClassPathRoutesLoader(String filePath) {
		this(new File(filePath));
	}

	public ClassPathRoutesLoader(File file) {
		this.file = file;
	}
	
	public ClassPathRoutesLoader(InputStream inputStream) {
		this.inputStream = inputStream;
	}
	
	@Override
	protected InputStream getInputStream() throws Exception {
		if(null != this.inputStream){
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
