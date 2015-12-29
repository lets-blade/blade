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

public final class ResponsePrint {
	
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
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final PrintWriter writer = new PrintWriter(baos);
        err.printStackTrace(writer);
        writer.close();
        response.setStatus(code);
        InputStream body = new ByteArrayInputStream(baos.toByteArray());
        try {
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
    
}
