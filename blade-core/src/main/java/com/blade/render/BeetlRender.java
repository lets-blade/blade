package com.blade.render;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Set;

import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.exception.BeetlException;
import org.beetl.core.resource.WebAppResourceLoader;

import com.blade.Blade;
import com.blade.context.BladeWebContext;
import com.blade.http.Request;
import com.blade.servlet.Session;

import blade.kit.log.Logger;

/**
 * Beetl渲染引擎
 * @author biezhi
 *
 */
public class BeetlRender implements Render {
    
	private static final Logger LOGGER = Logger.getLogger(BeetlRender.class);
	
	private GroupTemplate groupTemplate = null;
	
	private Blade blade;
	/**
	 * 默认构造函数
	 */
	public BeetlRender() {
		try {
			blade = Blade.me();
			String root = blade.webRoot() + blade.viewPrefix();
			WebAppResourceLoader resourceLoader = new WebAppResourceLoader();
			resourceLoader.setAutoCheck(true);
			resourceLoader.setRoot(root);
			Configuration cfg = Configuration.defaultConfiguration();
			groupTemplate = new GroupTemplate(resourceLoader, cfg);
		} catch (IOException e) {
			LOGGER.error(e);
		}
	}
	
	public BeetlRender(Configuration configuration) {
		try {
			blade = Blade.me();
			String root = blade.webRoot() + blade.viewPrefix();
			WebAppResourceLoader resourceLoader = new WebAppResourceLoader();
			resourceLoader.setAutoCheck(true);
			resourceLoader.setRoot(root);
			Configuration cfg = Configuration.defaultConfiguration();
			groupTemplate = new GroupTemplate(resourceLoader, cfg);
			groupTemplate.setConf(configuration);
		} catch (IOException e) {
			LOGGER.error(e);
		}
	}
	
	@Override
	public void render(ModelAndView modelAndView, Writer writer) throws RenderException {
		Request request = BladeWebContext.request();
		Session session = request.session();
		
		Template template = groupTemplate.getTemplate(modelAndView.getView());
		
		Map<String, Object> context = modelAndView.getModel();
		if(null != context && context.size() > 0){
			Set<String> keys = context.keySet();
			for(String key : keys){
				template.binding(key, context.get(key));
			}
		}
		
		Set<String> attrs = request.attributes();
		if(null != attrs && attrs.size() > 0){
			for(String attr : attrs){
				template.binding(attr, request.attribute(attr));
			}
		}
		
		Set<String> session_attrs = session.attributes();
		if(null != session_attrs && session_attrs.size() > 0){
			for(String attr : session_attrs){
				template.binding(attr, session.attribute(attr));
			}
		}
		try {
			template.renderTo(writer);
		} catch (BeetlException e) {
			e.printStackTrace();
			LOGGER.error(e);
		}
	}
}
