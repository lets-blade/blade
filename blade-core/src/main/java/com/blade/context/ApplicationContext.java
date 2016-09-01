package com.blade.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blade.Blade;
import com.blade.Const;
import com.blade.exception.BladeException;
import com.blade.ioc.IocApplication;

public final class ApplicationContext {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationContext.class);
	
	public static void init(Blade blade) throws BladeException{
		try {
			
			blade.bootstrap().init(blade);
			
			if(!blade.config().isInit()){
			    blade.loadAppConf(Const.APP_PROPERTIES);
				blade.config().setEnv(blade.environment());
		    }
			
			// initialization ioc
			IocApplication iocApplication = new IocApplication();
			iocApplication.initBeans();
			
			blade.init();
			blade.bootstrap().contextInitialized();
		} catch (Exception e) {
			LOGGER.error("ApplicationContext init error", e);
		}
		
	}
	
}
