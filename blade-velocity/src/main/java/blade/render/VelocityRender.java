package blade.render;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import blade.Blade;
import blade.BladeWebContext;
import blade.exception.BladeException;

/**
 * Velocity渲染引擎
 * @author biezhi
 *
 */
public class VelocityRender extends Render {
    
	private final VelocityEngine velocityEngine;
    
	/**
	 * 默认构造函数
	 */
	public VelocityRender() {
		Properties properties = new Properties();
		
		properties.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, Blade.webRoot());
		properties.setProperty(Velocity.ENCODING_DEFAULT, Blade.encoding());
		properties.setProperty(Velocity.INPUT_ENCODING, Blade.encoding());
		properties.setProperty(Velocity.OUTPUT_ENCODING, Blade.encoding());
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
			properties.put(Velocity.FILE_RESOURCE_LOADER_PATH, Blade.webRoot());
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
	public VelocityRender(VelocityEngine velocityEngine_) {
		velocityEngine = velocityEngine_;
	}
	
	/**
	 * 渲染视图
	 */
	@Override
	public Object render(String view) {
		
		HttpServletRequest servletRequest = BladeWebContext.servletRequest();
		HttpServletResponse servletResponse = BladeWebContext.servletResponse();
		
		try {
			PrintWriter writer = null;
			VelocityContext context = new VelocityContext();
			
			Enumeration<String> attrs = servletRequest.getAttributeNames();
			while (attrs.hasMoreElements()) {
				String attrName = attrs.nextElement();
				context.put(attrName, servletRequest.getAttribute(attrName));
			}
			
			view = disposeView(view);
			
			Template template = velocityEngine.getTemplate(view);
			
			writer = servletResponse.getWriter();
			
			template.merge(context, writer);
			
			writer.flush(); 
			writer.close();
			
		} catch (ResourceNotFoundException e) {
			render404(servletResponse, view);
		} catch (ParseErrorException e) {
			e.printStackTrace();
		} catch (MethodInvocationException e) {
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
		
		try {
			
			if(null == modelAndView){
				throw new BladeException("modelAndView is null");
			}
			
			PrintWriter writer = null;
			VelocityContext context = new VelocityContext(modelAndView.getModel());
			
			Enumeration<String> attrs = servletRequest.getAttributeNames();
			while (attrs.hasMoreElements()) {
				String attrName = attrs.nextElement();
				context.put(attrName, servletRequest.getAttribute(attrName));
			}
			
			String view = disposeView(modelAndView.getView());
			
			Template template = velocityEngine.getTemplate(view);
			
			writer = servletResponse.getWriter();
			
			template.merge(context, writer);
			
			writer.flush(); 
			writer.close();
			
		} catch (ResourceNotFoundException e) {
			render404(servletResponse, modelAndView.getView());
		} catch (ParseErrorException e) {
			e.printStackTrace();
		} catch (MethodInvocationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
