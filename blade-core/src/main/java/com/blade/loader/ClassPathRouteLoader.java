package com.blade.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * 
 * <p>
 * 基于ClassPath实现的路由加载器
 * </p>
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class ClassPathRouteLoader extends AbstractFileRouteLoader {

	private File file;

	private InputStream inputStream;
	
	public ClassPathRouteLoader() {
	}

	public ClassPathRouteLoader(String filePath) {
		this(new File(filePath));
	}

	public ClassPathRouteLoader(File file) {
		this.file = file;
	}
	
	public ClassPathRouteLoader(InputStream inputStream) {
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
