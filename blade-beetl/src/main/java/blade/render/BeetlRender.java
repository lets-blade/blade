package blade.render;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.exception.BeetlException;
import org.beetl.core.resource.WebAppResourceLoader;

import blade.Blade;
import blade.BladeWebContext;
import blade.exception.BladeException;

/**
 * Beetl渲染引擎
 * @author biezhi
 *
 */
public class BeetlRender extends Render {
    
	private GroupTemplate groupTemplate = null;
	
	/**
	 * 默认构造函数
	 */
	public BeetlRender() {
		try {
			String root = Blade.webRoot() + Blade.viewPath();
			WebAppResourceLoader resourceLoader = new WebAppResourceLoader();
			resourceLoader.setAutoCheck(true);
			resourceLoader.setRoot(root);
			Configuration cfg = Configuration.defaultConfiguration();
			groupTemplate = new GroupTemplate(resourceLoader, cfg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public BeetlRender(Configuration configuration) {
		try {
			String root = Blade.webRoot() + Blade.viewPath();
			WebAppResourceLoader resourceLoader = new WebAppResourceLoader();
			resourceLoader.setAutoCheck(true);
			resourceLoader.setRoot(root);
			Configuration cfg = Configuration.defaultConfiguration();
			groupTemplate = new GroupTemplate(resourceLoader, cfg);
			groupTemplate.setConf(configuration);
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
			
			Map<String, Object> context = modelAndView.getModel();
			
			Enumeration<String> attrs = servletRequest.getAttributeNames();
			
			if(null != attrs && attrs.hasMoreElements()){
				while(attrs.hasMoreElements()){
					String attr = attrs.nextElement();
					template.binding(attr, servletRequest.getAttribute(attr));
				}
			}
			
			if(null != context && context.size() > 0){
				Set<String> keys = context.keySet();
				for(String key : keys){
					template.binding(key, context.get(key));
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
	 * 处理视图
	 * @param view	视图名称
	 * @return		返回取出多余"/"的全路径
	 */
	String disposeView(String view){
		if(null != view){
			view = view.replaceAll("[/]+", "/");
			if(!view.endsWith(Blade.viewExt())){
				view = view + Blade.viewExt();
			}
		}
		return view;
	}
}
