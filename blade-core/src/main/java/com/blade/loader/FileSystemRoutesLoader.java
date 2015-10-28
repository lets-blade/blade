package com.blade.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class FileSystemRoutesLoader extends AbstractFileRoutesLoader {

	private File file;

	public FileSystemRoutesLoader() {
	}

	public FileSystemRoutesLoader(String filePath) {
		this(new File(filePath));
	}

	public FileSystemRoutesLoader(File file) {
		this.file = file;
	}

	@Override
	protected InputStream getInputStream() throws Exception {
		return new FileInputStream(file);
	}

	public void setFile(File file) {
		this.file = file;
	}

	public void setFilePath(String filePath) {
		this.file = new File(filePath);
	}
}
