package blade.render;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.exception.BeetlException;
import org.beetl.core.resource.FileResourceLoader;

import blade.Blade;
import blade.BladeWebContext;
import blade.exception.BladeException;

/**
 * Velocity渲染引擎
 * @author biezhi
 *
 */
public class BeetlRender extends Render {
    
	private GroupTemplate groupTemplate = null;
	
	/**
	 * 默认构造函数
	 */
	public BeetlRender() {
		String root = Blade.webRoot();
		FileResourceLoader resourceLoader = new FileResourceLoader(root,"utf-8");
		try {
			Configuration cfg = Configuration.defaultConfiguration();
			groupTemplate = new GroupTemplate(resourceLoader, cfg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 渲染视图
	 */
	@Override
	public Object render(String view) {
		
		try {
			HttpServletRequest servletRequest = BladeWebContext.servletRequest();
			HttpServletResponse servletResponse = BladeWebContext.servletResponse();
			
			view = disposeView(view);
			
			Template template = groupTemplate.getTemplate(view);
			
			Enumeration<String> attrs = servletRequest.getAttributeNames();
			
			if(null != attrs && attrs.hasMoreElements()){
				while(attrs.hasMoreElements()){
					String attr = attrs.nextElement();
					template.binding(attr, servletRequest.getAttribute(attr));
				}
			}
			
			template.renderTo(servletResponse.getOutputStream());
		} catch (BeetlException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * 渲染视图
	 */
	@Override
	public Object render(ModelAndView modelAndView) {
		HttpServletRequest servletRequest = BladeWebContext.servletRequest();
		HttpServletResponse servletResponse = BladeWebContext.servletResponse();
		
		if(null == modelAndView){
			throw new BladeException("modelAndView is null");
		}
		
		try {
			
			String view = disposeView(modelAndView.getView());
			
			Template template = groupTemplate.getTemplate(view);
			
			Enumeration<String> attrs = servletRequest.getAttributeNames();
			
			if(null != attrs && attrs.hasMoreElements()){
				while(attrs.hasMoreElements()){
					String attr = attrs.nextElement();
					template.binding(attr, servletRequest.getAttribute(attr));
				}
			}
			
			template.renderTo(servletResponse.getOutputStream());
		} catch (BeetlException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
}
