package com.blade.web;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import com.blade.Blade;
import com.blade.Const;
import com.blade.web.http.HttpException;
import com.blade.web.http.Response;

import blade.kit.FileKit;
import blade.kit.StreamKit;
import blade.kit.StringKit;

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
	
	private static Boolean isDev = null;
	
	/**
	 * Print Error Message
	 * @param err
	 * @param code
	 * @param response
	 */
	public static void printError(Throwable err, int code, Response response){
		if(null == isDev){
			isDev = Blade.me().isDev();
		}
		err.printStackTrace();
		try {
			final ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        final PrintWriter writer = new PrintWriter(baos);
	        
			// If the developer mode, the error output to the page
			if(isDev){
				writer.println(String.format(HTML, err.getClass() + " : " + err.getMessage()));
				writer.println();
				err.printStackTrace(writer);
				writer.println(END);
			} else {
				if(code == 404){
					String view404 = Blade.me().view404();
					if(StringKit.isNotBlank(view404)){
						response.render(view404);
						return;
					} else {
						writer.write(err.getMessage());
					}
				} else {
					String view500 = Blade.me().view500();
					if(StringKit.isNotBlank(view500)){
						response.render(view500);
						return;
					} else {
						writer.write(Const.INTERNAL_ERROR);
					}
				}
			}
			writer.close();
	        response.status(code);
	        InputStream body = new ByteArrayInputStream(baos.toByteArray());
			print(body, response.outputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Print
	 * @param body
	 * @param out
	 * @throws IOException
	 */
	public static void print(InputStream body, OutputStream out) throws IOException {
		StreamKit.io(body, out, true, true);
		/*
		try {
			int size = in.available();
			byte[] content = new byte[size];
			in.read(content);
			out.write(content);
        } finally {
        	in.close();
    		out.close();
        }*/
	}
	
	
	/**
	 * Print static file
	 * @param uri
	 * @param realpath
	 * @param httpResponse
	 */
	public static void printStatic(String uri, String realpath, Response response) {
		try {
			File file = new File(realpath);
    		if(FileKit.exist(file)){
    			FileInputStream in = new FileInputStream(file);
    			print(in, response.outputStream());
    		} else {
    			HttpException httpException = new HttpException(404, uri + " not found");
    			DispatchKit.printError(httpException, 404, response);
			}
		} catch (FileNotFoundException e) {
			DispatchKit.printError(e, 404, response);
		} catch (IOException e) {
			DispatchKit.printError(e, 500, response);
		}
	}
    
	private static final String HTML = "<!DOCTYPE html><html><head><meta charset='utf-8'><title>Blade Framework Error Page</title>"
			+ "<style type='text/css'>*{margin:0;padding:0}.info{margin:0;padding:10px;color:#000;background-color:#f8edc2;height:60px;line-height:60px;border-bottom:5px solid #761226}.isa_error{margin:0;padding:10px;font-size:14px;font-weight:bold;background-color:#e0c9db;border-bottom:1px solid #000}.version{color:green;font-size:16px;font-weight:bold;padding:10px}</style></head><body>"
			+ "<div class='info'><h3>%s</h3></div><div class='isa_error'><pre>";
	
	
	private static final String END = "</pre></div><div class='version'>Blade-" + Const.BLADE_VERSION + "（<a href='http://bladejava.com' target='_blank'>Blade Framework</a>） </div></body></html>";
	
}
