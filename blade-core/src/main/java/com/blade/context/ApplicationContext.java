package com.blade.context;

import com.blade.Blade;
import com.blade.Const;
import com.blade.aop.ProxyClassLoader;
import com.blade.exception.BladeException;
import com.blade.ioc.IocApplication;
import com.blade.kit.resource.BladeClassLoader;

public final class ApplicationContext {

	public static void init(Blade blade) throws BladeException{
		
		blade.bootstrap().init(blade);
		
		if(!blade.config().isInit()){
		    blade.loadAppConf(Const.APP_PROPERTIES);
			blade.config().setEnv(blade.environment());
	    }
		
		// load config
		blade.configLoader().loadConfig();
		
	    // buiding route
		blade.routeBuilder().building();
		
		// initialization ioc
		IocApplication iocApplication = new IocApplication();
		iocApplication.init();
		blade.init();
		
		blade.bootstrap().contextInitialized();
		
	}
	
	private static final BladeClassLoader CLASSLOADER = new ProxyClassLoader();
	
	public static BladeClassLoader getClassLoader(){
		return CLASSLOADER;
	}
	
}
