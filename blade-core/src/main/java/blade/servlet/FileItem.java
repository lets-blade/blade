/**
 * Copyright (c) 2015, biezhi 王爵 (biezhi.me@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package blade.servlet;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import blade.kit.io.FastByteArrayOutputStream;

/**
 * 文件上传Item
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class FileItem {

	private String name;

	private boolean isFile;
	private String fileName;
	private String contentType;
	private InputStream inputStream;
	
    public static final String FORM_DATA = "form-data";

    public static final String MULTIPART_FORM_DATA = "multipart/form-data";

    // 数据体缓存
	private FastByteArrayOutputStream outputStream;
	
	public void write(byte[] buf, int off, int len) {
		if (outputStream == null) {
			outputStream = new FastByteArrayOutputStream();
		}
		outputStream.write(buf, off, len);
	}

	// -----------------------------------------------------------------
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public boolean isFile() {
		return isFile;
	}

	public void setFile(boolean isFile) {
		this.isFile = isFile;
	}

	public byte[] getFileContent() {
		byte[] buf = outputStream.toByteArray();
		int dirtyCount = 2;

		// 最后会多出一个\r\n,
		// 根据ServletInputStream, \n 就算一行结束, 因此对于\r需要特殊判断
		if ('\r' != buf[buf.length - 2])
			dirtyCount = 1;

		return Arrays.copyOfRange(buf, 0, buf.length - dirtyCount);
	}

	public String getString(String encoding){
		try {
			return outputStream == null ? null : new String(getFileContent(), encoding);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String getString() {
		return outputStream == null ? null : new String(getFileContent());
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}
	
}

