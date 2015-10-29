package com.blade.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * 
 * <p>
 * 基于文件系统实现的路由加载器
 * </p>
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class FileSystemRouteLoader extends AbstractFileRouteLoader {

	private File file;

	public FileSystemRouteLoader() {
	}

	public FileSystemRouteLoader(String filePath) {
		this(new File(filePath));
	}

	public FileSystemRouteLoader(File file) {
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
