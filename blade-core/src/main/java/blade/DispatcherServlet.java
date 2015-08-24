package blade;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import blade.kit.log.Logger;
import blade.route.RouteMatcherBuilder;

/**
 * blade核心调度器，mvc总线
 * 匹配所有Blade请求
 * 
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class DispatcherServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
       
	private static final Logger LOGGER = Logger.getLogger(DispatcherServlet.class);

	/**
	 * blade全局初始化类
	 */
    private static final String APPLCATION_CLASS = "bootstrapClass";
    
	@Override
	public void init() throws ServletException {
		super.init();
		// 防止重复初始化
    	try {
			if(!Blade.IS_INIT){
				
				BladeBase.webRoot(this.getServletContext().getRealPath("/"));
				
				BladeWebContext.servletContext(this.getServletContext());
				
			    final Bootstrap application = getBootstrap(this.getInitParameter(APPLCATION_CLASS));
			    application.init();
			    Blade.app(application);
			    
			    // 构建路由
			    RouteMatcherBuilder.building();
			    
			    IocApplication.init();
			    
			    application.contextInitialized(BladeWebContext.servletContext());
			    
			    BladeBase.init();
			    
			    LOGGER.info("blade init complete!");
			}
		} catch (Exception e) {
			LOGGER.error(e);
			System.exit(0);
		}
	}
	
	/**
     * 获取全局初始化对象，初始化应用
     * 
     * @param botstrapClassName 		全局初始化类名
     * @return 							一个全局初始化对象
     * @throws ServletException
     */
    private Bootstrap getBootstrap(String botstrapClassName) throws ServletException {
    	Bootstrap bootstrapClass = null;
        try {
        	if(null != botstrapClassName){
            	Class<?> applicationClass = Class.forName(botstrapClassName);
                if(null != applicationClass){
                	bootstrapClass = (Bootstrap) applicationClass.newInstance();
                }
        	} else {
        		throw new ServletException("bootstrapClass is null !");
			}
        } catch (Exception e) {
            throw new ServletException(e);
        }
		return bootstrapClass;
    }
    
    public DispatcherServlet() {
        super();
    }

    @Override
    protected void service(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
    		throws ServletException, IOException {

        httpRequest.setCharacterEncoding(BladeBase.encoding());
        httpResponse.setCharacterEncoding(BladeBase.encoding());
        
        /**
         * 是否被RequestHandler执行
         */
        boolean isHandler = RequestHandler.single().handler(httpRequest, httpResponse);
        if(!isHandler && !httpResponse.isCommitted()){
        	super.service(httpRequest, httpResponse);
        }
    }
    
    @Override
    public void destroy() {
    	IocApplication.destroy();
    	LOGGER.info("blade destroy!");
    }
}
