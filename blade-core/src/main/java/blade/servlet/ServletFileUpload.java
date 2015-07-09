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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import blade.route.HttpMethod;

/**
 * 文件上传对象
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class ServletFileUpload {
	
	private static int BUF_SIZE = 1024 * 1024;

	private HttpServletRequest request;
	
	private static final String MULTIPART = "multipart/";
	
	/**
	 * 所有的上传文本域
	 */
	private Map<String, List<FileItem>> allFileItems;

	private ServletInputStream in;
	private byte[] buf;
	private String line;
	
	private ServletFileUpload(HttpServletRequest request) {
		this.request = request;
	}
	
	public boolean isMultipartContent(HttpServletRequest request) {
        if (!HttpMethod.POST.toString().equalsIgnoreCase(request.getMethod())) {
            return false;
        }
        String contentType = request.getContentType();
        if (contentType == null) {
            return false;
        }
        if (contentType.toLowerCase(Locale.ENGLISH).startsWith(MULTIPART)) {
            return true;
        }
        return false;
    }
	
	/**
	 * 获取一个普通文本参数
	 * 
	 * @param name	参数名称
	 * @return		返回要获取的参数
	 */
	public String param(String name) {
		FileItem part = fileItem(name);
		return part != null && !part.isFile() ? part.getString() : null;
	}

	/**
	 * 获取多个普通文本参数
	 * 
	 * @param name	参数名称
	 * @return		返回要获取的参数数组
	 */
	public String[] params(String name) {
		String[] params = null;

		FileItem[] parts = fileItems(name);

		if (parts != null) {
			params = new String[parts.length];
			for (int i = 0; i < parts.length; i++) {
				FileItem part = parts[i];
				params[i] = part.isFile() ? part.getString() : null;
			}
		}

		return params;
	}

	/**
	 * 获取一个参数
	 * 
	 * @param name	参数名称
	 * @return		返回FileItem对象
	 */
	public FileItem fileItem(String name) {
		FileItem[] parts = fileItems(name);
		return parts == null ? null : parts[0];
	}

	/**
	 * 获取多个个参数
	 * 
	 * @param name	参数名称
	 * @return		返回FileItem对象数组
	 */
	public FileItem[] fileItems(String name) {
		try {
			if (allFileItems == null)
				parseMultiFileItem();

			if (allFileItems == null)
				return null;

			List<FileItem> list = allFileItems.get(name);
			if (list == null || list.size() == 0)
				return null;

			return list.toArray(new FileItem[list.size()]);
		} catch (IOException e) {
			// quiet
		}

		return null;
	}
	
	/**
	 * 设置缓冲大小
	 * 
	 * @param buffer	缓冲大小，用字节表示
	 */
	public void setBufferSize(final int buffer){
		BUF_SIZE = buffer;
	}
	
	public void parseMultiFileItem() throws IOException {
		in = request.getInputStream();
		buf = new byte[BUF_SIZE];

		String contentType = request.getContentType();
		int pos = contentType.indexOf("boundary=");
		String boundary = contentType.substring(pos + 9);

		FileItem fileItem = null;

		int len = -1;
		while ((len = readLine()) != -1) {
			if (line.endsWith(boundary)) { // 开始一个域
				
				putFileItem(fileItem);

				// 域名
				readLine();
				fileItem = checkFileItem(line); // disposition

				if (fileItem.isFile()) {
					// 若是文件类型, 文件类型描述
					readLine();
					String type = line.substring("Content-Type: ".length());
					fileItem.setContentType(type);
				}
				
				// 开始数据体前会有一个空行
				readLine();
				continue;

			} else if (line.indexOf(boundary) > -1) { // 全部结束
				putFileItem(fileItem);
				break;
			}

			// 数据体处理
			fileItem.write(buf, 0, len);
			fileItem.setInputStream(in);
		}
	}
	

	/**
	 * 产生一个域对象, 并判断是不是文件
	 * 
	 * @param disposition	匹配disposition
	 * @return				返回FileItem对象
	 */
	private FileItem checkFileItem(String disposition) {
		String regexFile = "^Content-Disposition: form-data; name=\"(.+)\"; filename=\"(.+)\"$";
		String regexComm = "^Content-Disposition: form-data; name=\"(.+)\"$";

		FileItem fileItem = new FileItem();

		// 文件域
		Matcher m = Pattern.compile(regexFile).matcher(disposition);
		if (m.find()) {
			fileItem.setFile(true);
			fileItem.setName(m.group(1));
			fileItem.setFileName(m.group(2));
			return fileItem;
		}

		// 普通文本域
		m = Pattern.compile(regexComm).matcher(disposition);
		if (m.find()) {
			fileItem.setFile(false);
			fileItem.setName(m.group(1));
			return fileItem;
		}

		return null;
	}

	/**
	 * @return	读取一行到缓冲区, 返回读取字节数
	 * @throws 	IOException
	 */
	private int readLine() throws IOException {
		int len = in.readLine(buf, 0, buf.length);
		line = new String(buf, 0, len).trim();

		return len;
	}

	/**
	 * 临时保存域对象
	 * 
	 * @param FileItem	FileItem对象
	 */
	private void putFileItem(FileItem fileItem) {
		if (fileItem == null)
			return;

		if (allFileItems == null) {
			allFileItems = new HashMap<String, List<FileItem>>();
		}

		List<FileItem> list = allFileItems.get(fileItem.getName());
		if (list == null) {
			list = new ArrayList<FileItem>();
			allFileItems.put(fileItem.getName(), list);
		}
		
		list.add(fileItem);
	}

	/**
	 * 生成一个上传文件对象
	 * 
	 * @param servletRequest	请求对象，用于解析表单文件
	 * @return					返回一个上传文件对象
	 */
	public static ServletFileUpload parseRequest(HttpServletRequest servletRequest) {
		return new ServletFileUpload(servletRequest);
	}

}