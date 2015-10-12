package blade.render;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.exception.BeetlException;
import org.beetl.core.resource.WebAppResourceLoader;

import com.blade.BladeWebContext;
import com.blade.render.ModelAndView;
import com.blade.render.Render;
import com.blade.servlet.Request;
import com.blade.servlet.Response;
import com.blade.servlet.Session;

import blade.exception.BladeException;
import blade.kit.log.Logger;

/**
 * Beetl渲染引擎
 * @author biezhi
 *
 */
public class BeetlRender extends Render {
    
	private static final Logger LOGGER = Logger.getLogger(BeetlRender.class);
	
	private GroupTemplate groupTemplate = null;
	
	/**
	 * 默认构造函数
	 */
	public BeetlRender() {
		try {
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
	
	/**
	 * 渲染视图
	 */
	@Override
	public Object render(String view) {
		
		Response response = BladeWebContext.response();
		try {
			Request request = BladeWebContext.request();
			Session session = BladeWebContext.session();
			
			view = disposeView(view);
			
			Template template = groupTemplate.getTemplate(view);
			
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
			
			template.renderTo(response.outputStream());
		} catch (BeetlException e) {
			LOGGER.error(e);
		} catch (IOException e) {
			LOGGER.error(e);
		}
		return null;
	}


	/**
	 * 渲染视图
	 */
	@Override
	public Object render(ModelAndView modelAndView) {
		
		Response response = BladeWebContext.response();
		try {
			
			Request request = BladeWebContext.request();
			Session session = BladeWebContext.session();
			
			if(null == modelAndView){
				throw new BladeException("modelAndView is null");
			}
			
			String view = disposeView(modelAndView.getView());
			
			Template template = groupTemplate.getTemplate(view);
			
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
			
			template.renderTo(response.outputStream());
		} catch (BeetlException e) {
			LOGGER.error(e);
		} catch (IOException e) {
			LOGGER.error(e);
		}
		
		return null;
	}
	
	/**
	 * 处理视图
	 * @param view	视图名称
	 * @return		返回取出多余"/"的全路径
	 */
	@Override
	protected String disposeView(String view){
		if(null != view){
			view = view.replaceAll("[/]+", "/");
			if(!view.endsWith(blade.viewSuffix())){
				view = view + blade.viewSuffix();
			}
		}
		return view;
	}
}
