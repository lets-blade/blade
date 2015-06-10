package blade.kit;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

/**
 * 文件上传
 * 
 * @author biezhi
 * @since 1.0
 *
 */
public class FileUpload {
	
	private static final int BUF_SIZE = 1024 * 1024;

	private HttpServletRequest request;
	
	/**
	 * 所有的上传文本域
	 */
	private Map<String, List<FilePart>> allFileParts;

	private ServletInputStream in;
	private byte[] buf;
	private String line;

	public FileUpload(HttpServletRequest request) {
		this.request = request;
	}
	
	/**
	 * 获取一个普通文本参数
	 * 
	 * @param name	参数名称
	 * @return		返回要获取的参数
	 */
	public String getParam(String name) {
		FilePart part = getFilePart(name);
		return part != null && !part.isFile() ? part.getValue() : null;
	}

	/**
	 * 获取多个普通文本参数
	 * 
	 * @param name	参数名称
	 * @return		返回要获取的参数数组
	 */
	public String[] getParams(String name) {
		String[] params = null;

		FilePart[] parts = getFileParts(name);

		if (parts != null) {
			params = new String[parts.length];
			for (int i = 0; i < parts.length; i++) {
				FilePart part = parts[i];
				params[i] = part.isFile() ? part.getValue() : null;
			}
		}

		return params;
	}

	/**
	 * 获取一个参数
	 * 
	 * @param name	参数名称
	 * @return		返回FilePart对象
	 */
	public FilePart getFilePart(String name) {
		FilePart[] parts = getFileParts(name);
		return parts == null ? null : parts[0];
	}

	/**
	 * 获取多个个参数
	 * 
	 * @param name	参数名称
	 * @return		返回FilePart对象数组
	 */
	public FilePart[] getFileParts(String name) {
		try {
			if (allFileParts == null)
				parseMultiFilePart();

			if (allFileParts == null)
				return null;

			List<FilePart> list = allFileParts.get(name);
			if (list == null || list.size() == 0)
				return null;

			return list.toArray(new FilePart[list.size()]);
		} catch (IOException e) {
			// quiet
		}

		return null;
	}

	/**
	 * 解析上传表单
	 * @throws IOException
	 */
	private void parseMultiFilePart() throws IOException {
		in = request.getInputStream();
		buf = new byte[BUF_SIZE];

		String contentType = request.getContentType();
		int pos = contentType.indexOf("boundary=");
		String boundary = contentType.substring(pos + 9);

		FilePart part = null;

		int len = -1;
		while ((len = readLine()) != -1) {
			if (line.endsWith(boundary)) { // 开始一个域
				putFilePart(part);

				// 域名
				readLine();
				part = checkFilePart(line); // disposition

				if (part.isFile()) {
					// 若是文件类型, 文件类型描述
					readLine();
					String type = line.substring("Content-Type: ".length());
					part.setContentType(type);
				}

				// 开始数据体前会有一个空行
				readLine();
				continue;

			} else if (line.indexOf(boundary) > -1) { // 全部结束
				putFilePart(part);
				break;
			}

			// 数据体处理
			part.write(buf, 0, len);
		}
	}

	/**
	 * 产生一个域对象, 并判断是不是文件
	 * 
	 * @param disposition	匹配disposition
	 * @return				返回FilePart对象
	 */
	private FilePart checkFilePart(String disposition) {
		String regexFile = "^Content-Disposition: form-data; name=\"(.+)\"; filename=\"(.+)\"$";
		String regexComm = "^Content-Disposition: form-data; name=\"(.+)\"$";

		FilePart FilePart = new FilePart();

		// 文件域
		Matcher m = Pattern.compile(regexFile).matcher(disposition);
		if (m.find()) {
			FilePart.setFile(true);
			FilePart.setName(m.group(1));
			FilePart.setFileName(m.group(2));
			return FilePart;
		}

		// 普通文本域
		m = Pattern.compile(regexComm).matcher(disposition);
		if (m.find()) {
			FilePart.setFile(false);
			FilePart.setName(m.group(1));
			return FilePart;
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
	 * @param FilePart	FilePart对象
	 */
	private void putFilePart(FilePart FilePart) {
		if (FilePart == null)
			return;

		if (allFileParts == null) {
			allFileParts = new HashMap<String, List<FilePart>>();
		}

		List<FilePart> list = allFileParts.get(FilePart.getName());
		if (list == null) {
			list = new ArrayList<FilePart>();
			allFileParts.put(FilePart.getName(), list);
		}

		list.add(FilePart);
	}

	/**
	 * 上传表单里面的一个域
	 * 
	 * @author biezhi
	 * @since 1.0
	 *
	 */
	public static class FilePart {
		private String name;

		private boolean isFile;
		private String fileName;
		private String contentType;

		// 数据体缓存
		private ByteArrayOutputStream temp;

		public void write(byte[] buf, int off, int len) {
			if (temp == null) {
				temp = new ByteArrayOutputStream();
			}
			temp.write(buf, off, len);
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
			byte[] buf = temp.toByteArray();
			int dirtyCount = 2;

			// 最后会多出一个\r\n,
			// 根据ServletInputStream, \n 就算一行结束, 因此对于\r需要特殊判断
			if ('\r' != buf[buf.length - 2])
				dirtyCount = 1;

			return Arrays.copyOfRange(buf, 0, buf.length - dirtyCount);
		}

		public String getValue() {
			return temp == null ? null : new String(getFileContent());
		}
	}
}