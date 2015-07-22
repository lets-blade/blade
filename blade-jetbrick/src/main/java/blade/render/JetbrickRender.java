package blade.render;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import jetbrick.io.resource.ResourceNotFoundException;
import jetbrick.template.JetEngine;
import jetbrick.template.JetTemplate;
import blade.Blade;
import blade.BladeWebContext;
import blade.exception.BladeException;
import blade.kit.log.Logger;
import blade.servlet.Request;
import blade.servlet.Response;
import blade.servlet.Session;

/**
 * Velocity渲染引擎
 * @author biezhi
 *
 */
public class JetbrickRender extends Render {
    
	private static final Logger LOGGER = Logger.getLogger(JetbrickRender.class);
			
	private JetEngine jetEngine;
    
	private Properties config;
	
	/**
	 * 默认构造函数
	 */
	public JetbrickRender() {
		config = new Properties();
		config.put("jetx.input.encoding", Blade.encoding());
		config.put("jetx.output.encoding", Blade.encoding());
		config.put("jetx.template.suffix", ".html");
		config.put("jetx.template.loaders", "jetbrick.template.loader.FileSystemResourceLoader");
		
		jetEngine = JetEngine.create(config);
	}
	
	/**
	 * @return	返回JetEngine引擎
	 */
	public JetEngine getJetEngine(){
		return jetEngine;
	}
	
	/**
	 * 清空配置
	 */
	public void clean(){
		if(null != config){
			config.clear();
		}
	}
	
	/**
	 * 添加一个配置
	 * @param key	配置键
	 * @param value	配置值
	 */
	public void put(String key, Object value){
		if(null == config){
			config = new Properties();
		}
		config.put(key, value);
	}
	
	/**
	 * 根据配置文件构造一个JetEngine引擎
	 * @param configLocation
	 * @throws IOException 
	 */
	public JetbrickRender(String configLocation) throws IOException {
		jetEngine = JetEngine.create(configLocation);
	}
	
	/**
	 * 根据构造一个JetEngine引擎
	 * @param config	Properties配置
	 */
	public JetbrickRender(Properties config) {
		this.config = config;
		jetEngine = JetEngine.create(this.config);
	}
	
	/**
	 * 手动构造JetEngine引擎
	 * @param jetEngine	jetEngine引擎
	 */
	public JetbrickRender(JetEngine jetEngine) {
		this.jetEngine = jetEngine;
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
			
			view = Blade.webRoot() + disposeView(view);
			
			JetTemplate template = jetEngine.getTemplate(view);
			
			Map<String, Object> context = new HashMap<String, Object>();
			
			Set<String> attrs = request.attributes();
			if(null != attrs && attrs.size() > 0){
				for(String attr : attrs){
					context.put(attr, request.attribute(attr));
				}
			}
			
			Set<String> session_attrs = session.attributes();
			if(null != session_attrs && session_attrs.size() > 0){
				for(String attr : session_attrs){
					context.put(attr, session.attribute(attr));
				}
			}
			
			template.render(context, response.outputStream());
		} catch (ResourceNotFoundException e) {
			render404(response, view);
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
			
			String view = Blade.webRoot() + disposeView(modelAndView.getView());
			
			JetTemplate template = jetEngine.getTemplate(view);
			
			Map<String, Object> context = modelAndView.getModel();
			
			Set<String> attrs = request.attributes();
			if(null != attrs && attrs.size() > 0){
				for(String attr : attrs){
					context.put(attr, request.attribute(attr));
				}
			}
			
			Set<String> session_attrs = session.attributes();
			if(null != session_attrs && session_attrs.size() > 0){
				for(String attr : session_attrs){
					context.put(attr, session.attribute(attr));
				}
			}
			
			template.render(context, response.outputStream());
			
		} catch (ResourceNotFoundException e) {
			render404(response, modelAndView.getView());
		} catch (IOException e) {
			LOGGER.error(e);
		}
		return null;
	}
	
}
