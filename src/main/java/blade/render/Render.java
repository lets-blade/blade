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
package blade.render;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import blade.Blade;
import blade.WebContext;


/**
 * 渲染器抽象类
 * 
 * @author	biezhi
 * @since	1.0
 *
 */
public abstract class Render {
	
	static final String VIEW_NOTFOUND = "<html><body><h2>404 %s</h2></body></html>";
	
	public void render404(String viewName){
		render404(null, viewName);
	}
	
	/**
	 * 404视图
	 * @param httpResponse
	 * @param viewName
	 */
	public void render404(HttpServletResponse httpResponse, String viewName){
        try {
        	String view404 = Blade.view404();
        	if(null != view404){
        		ModelAndView modelAndView = new ModelAndView(view404);
        		modelAndView.add("viewName", viewName);
        		render(modelAndView);
        	} else {
        		if(null == httpResponse){
        			httpResponse = WebContext.servletResponse();
        		}
        		
            	httpResponse.setContentType("text/html; charset=utf-8");
            	httpResponse.setStatus(404);
    			ServletOutputStream outputStream = httpResponse.getOutputStream();
    			outputStream.print(String.format(VIEW_NOTFOUND, viewName + " Not Found"));
    			outputStream.flush();
    			outputStream.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void render500(String bodyContent){
	    try {
	    	
	    	String view500 = Blade.view500();
        	if(null != view500){
        		ModelAndView modelAndView = new ModelAndView(view500);
        		modelAndView.add("body", bodyContent);
        		render(modelAndView);
        	} else {
        		HttpServletResponse httpResponse = WebContext.servletResponse();
    	    	
    	    	httpResponse.setContentType("text/html; charset=utf-8");
    			ServletOutputStream outputStream = httpResponse.getOutputStream();

    			outputStream.print(bodyContent.toString());
    			outputStream.flush();
    			outputStream.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
     * 输出json
     * @param json
     */
    public void json(String json){
    	if(null != json){
    		HttpServletResponse response = WebContext.servletResponse();
    		HttpServletRequest request = WebContext.servletRequest();
    		
    		response.setHeader("Cache-Control", "no-cache");
    		String userAgent = request.getHeader("User-Agent");
    		if (userAgent.contains("MSIE")) {
    			response.setContentType("text/html;charset=utf-8");
    		} else {
    			response.setContentType("application/json;charset=utf-8");
    		}
    		try {
    			request.setCharacterEncoding("utf-8");
    			PrintWriter out = response.getWriter();
    			out.print(json.toString());
        		out.flush();
        		out.close();
    		} catch (UnsupportedEncodingException e1) {
    			e1.printStackTrace();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    }
    
    /**
     * 输出text
     * @param text
     */
    public void text(String text){
    	if(null != text){
    		HttpServletResponse response = WebContext.servletResponse();
    		HttpServletRequest request = WebContext.servletRequest();
    		response.setHeader("Cache-Control", "no-cache");
    		response.setContentType("text/plain;charset=utf-8");
    		try {
    			request.setCharacterEncoding("utf-8");
    			PrintWriter out = response.getWriter();
    			out.print(text);
        		out.flush();
        		out.close();
    		} catch (UnsupportedEncodingException e1) {
    			e1.printStackTrace();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    }
    
    /**
     * 输出xml
     * @param json
     */
    public void xml(String xml){
    	if(null != xml){
    		HttpServletResponse response = WebContext.servletResponse();
    		HttpServletRequest request = WebContext.servletRequest();
    		response.setHeader("Cache-Control", "no-cache");
    		response.setContentType("text/xml;charset=utf-8");
    		try {
    			request.setCharacterEncoding("utf-8");
    			PrintWriter out = response.getWriter();
    			out.print(xml);
        		out.flush();
        		out.close();
    		} catch (UnsupportedEncodingException e1) {
    			e1.printStackTrace();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    }
    
    /**
     * 输出HTML
     * @param html
     */
    public void html(String html){
    	if(null != html){
    		HttpServletResponse response = WebContext.servletResponse();
    		HttpServletRequest request = WebContext.servletRequest();
    		response.setHeader("Cache-Control", "no-cache");
    		response.setContentType("text/html;charset=utf-8");
    		try {
    			request.setCharacterEncoding("utf-8");
    			PrintWriter out = response.getWriter();
    			out.print(html);
        		out.flush();
        		out.close();
    		} catch (UnsupportedEncodingException e1) {
    			e1.printStackTrace();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    }
    
    /**
     * 输出javascript
     * @param javascript
     */
    public void javascript(String javascript){
    	if(null != javascript){
    		HttpServletResponse response = WebContext.servletResponse();
    		HttpServletRequest request = WebContext.servletRequest();
    		response.setHeader("Cache-Control", "no-cache");
    		response.setContentType("text/javascript;charset=utf-8");
    		try {
    			request.setCharacterEncoding("utf-8");
    			PrintWriter out = response.getWriter();
    			out.print(javascript);
        		out.flush();
        		out.close();
    		} catch (UnsupportedEncodingException e1) {
    			e1.printStackTrace();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    }
    
	/**
	 * 处理视图
	 * 
	 * @param view
	 * @return
	 */
	String disposeView(String view){
		if(null != view){
			view = Blade.viewPath() + view;
			view = view.replaceAll("[/]+", "/");
			if(!view.endsWith(Blade.viewExt())){
				view = view + Blade.viewExt();
			}
		}
		return view;
	}
	
	public abstract Object render(final String view);
	
	public abstract Object render(ModelAndView modelAndView);

}
