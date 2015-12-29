package com.blade.web.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import blade.kit.FileKit;
import blade.kit.StreamKit;

import com.blade.Blade;
import com.blade.Const;

public final class ResponsePrint {
	
	private static boolean isDev = Blade.me().isDev();
	
	private ResponsePrint() {
	}
	
	/**
	 * Print Error Message
	 * @param err
	 * @param code
	 * @param response
	 */
	public static void printError(Throwable err, int code, HttpServletResponse response){
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
					writer.write(err.getMessage());
				} else {
					writer.write(Const.INTERNAL_ERROR);
				}
			}
			writer.close();
	        response.setStatus(code);
	        InputStream body = new ByteArrayInputStream(baos.toByteArray());
			print(body, response.getOutputStream());
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
	public static void printStatic(String uri, String realpath, HttpServletResponse httpResponse) {
		try {
			File file = new File(realpath);
    		if(FileKit.exist(file)){
    			FileInputStream in = new FileInputStream(file);
    			print(in, httpResponse.getOutputStream());
    		} else {
    			HttpException httpException = new HttpException(404, uri + " not found");
    			ResponsePrint.printError(httpException, 404, httpResponse);
			}
		} catch (FileNotFoundException e) {
			ResponsePrint.printError(e, 404, httpResponse);
		} catch (IOException e) {
			ResponsePrint.printError(e, 500, httpResponse);
		}
	}
    
	private static final String HTML = "<!DOCTYPE html><html><head><meta charset='utf-8'><title>Blade Framework Error Page</title>"
			+ "<style type='text/css'>*{margin:0;padding:0}.info{margin:0;padding:10px;color:#000;background-color:#f8edc2;height:60px;line-height:60px;border-bottom:5px solid #761226}.isa_error{margin:0;padding:10px;font-size:14px;font-weight:bold;background-color:#e0c9db;border-bottom:1px solid #000}.version{color:green;font-size:16px;font-weight:bold;padding:10px}</style></head><body>"
			+ "<div class='info'><h3>%s</h3></div><div class='isa_error'><pre>";
	
	
	private static final String END = "</pre></div><div class='version'>Blade-" + Const.BLADE_VERSION + "（<a href='http://bladejava.com' target='_blank'>Blade Framework</a>） </div></body></html>";
	
}
