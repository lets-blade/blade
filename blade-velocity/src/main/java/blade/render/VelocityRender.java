package blade.render;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.Set;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import com.blade.BladeWebContext;
import com.blade.render.ModelAndView;
import com.blade.render.Render;
import com.blade.servlet.Request;
import com.blade.servlet.Response;
import com.blade.servlet.Session;

import blade.exception.BladeException;
import blade.kit.log.Logger;

/**
 * Velocity渲染引擎
 * @author biezhi
 *
 */
public class VelocityRender extends Render {
    
	private static final Logger LOGGER = Logger.getLogger(VelocityRender.class);
	
	private final VelocityEngine velocityEngine;
    
	/**
	 * 默认构造函数
	 */
	public VelocityRender() {
		Properties properties = new Properties();
		
		properties.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, blade.webRoot());
		properties.setProperty(Velocity.ENCODING_DEFAULT, blade.encoding());
		properties.setProperty(Velocity.INPUT_ENCODING, blade.encoding());
		properties.setProperty(Velocity.OUTPUT_ENCODING, blade.encoding());
        velocityEngine = new VelocityEngine(properties);
	}
	
	/**
	 * 根据配置文件构造一个Velocity引擎
	 * @param propertiesFile
	 * @throws IOException 
	 */
	public VelocityRender(String propertiesFile) throws IOException {
		String loadPath = VelocityRender.class.getClassLoader().getResource("/").getPath();
		String fileName = loadPath + propertiesFile;
		
		Properties properties = new Properties();
		InputStream inStream = new FileInputStream(new File(fileName));
		properties.load(inStream);
		
		// 默认查询路径
		if(!properties.contains(Velocity.FILE_RESOURCE_LOADER_PATH)){
			properties.put(Velocity.FILE_RESOURCE_LOADER_PATH, blade.webRoot());
		}
        velocityEngine = new VelocityEngine(properties);
	}
	
	/**
	 * 根据构造一个Velocity引擎
	 * @param properties
	 */
	public VelocityRender(Properties properties) {
        velocityEngine = new VelocityEngine(properties);
	}
	
	/**
	 * 手动构造Velocity引擎
	 * @param velocityEngine_
	 */
	public VelocityRender(VelocityEngine velocityEngine) {
		this.velocityEngine = velocityEngine;
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
			
			VelocityContext context = new VelocityContext();
			
			Template template = velocityEngine.getTemplate(view);
			
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
			
			PrintWriter writer = response.writer();
			
			template.merge(context, writer);
			
			writer.flush(); 
			writer.close();
			
		} catch (ResourceNotFoundException e) {
			render404(response, view);
		} catch (ParseErrorException e) {
			LOGGER.error(e);
		} catch (MethodInvocationException e) {
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
			
			VelocityContext context = new VelocityContext(modelAndView.getModel());
			
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
			
			Template template = velocityEngine.getTemplate(view);
			
			PrintWriter writer = response.writer();
			
			template.merge(context, writer);
			
			writer.flush(); 
			writer.close();
			
		} catch (ResourceNotFoundException e) {
			render404(response, modelAndView.getView());
		} catch (ParseErrorException e) {
			LOGGER.error(e);
		} catch (MethodInvocationException e) {
			LOGGER.error(e);
		} catch (IOException e) {
			LOGGER.error(e);
		}
		return null;
	}
	
}
