package com.blade.web;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

public class DispatchKit {

	public static File getWebroot(ServletContext sc) {
		String dir = sc.getRealPath("/");
		if (dir == null) {
			try {
				URL url = sc.getResource("/");
				if (url != null && "file".equals(url.getProtocol())) {
					dir = URLDecoder.decode(url.getFile(), "utf-8");
				} else {
					throw new IllegalStateException("Can't get webroot dir, url = " + url);
				}
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}
		return new File(dir);
	}

	public static void setNoCache(HttpServletResponse response) {
		// Http 1.0 header
		response.setHeader("Buffer", "false");
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", 1L);
		// Http 1.1 header
		response.setHeader("Cache-Control", "no-cache, no-store, max-age=0");
	}

	public static void setFileDownloadHeader(HttpServletResponse response, String fileName, String contentType) {
		if (contentType == null) {
			contentType = "application/x-download";
		}
		response.setContentType(contentType);
		// 中文文件名支持
		try {
			String encodedfileName = new String(fileName.getBytes(), "ISO8859-1");
			response.setHeader("Content-Disposition", "attachment; filename=" + encodedfileName);
		} catch (UnsupportedEncodingException e) {
		}
	}
}
