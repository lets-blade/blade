/**
 * Copyright (c) 2016, biezhi 王爵 (biezhi.me@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blade.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blade.Blade;
import com.blade.Const;
import com.blade.exception.BladeException;
import com.blade.ioc.IocApplication;

/**
 * Blade ApplicationContext, init context
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.6.6
 */
public final class ApplicationContext {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationContext.class);
	
	public static void init(Blade blade) throws BladeException{
		try {
			
			blade.bootstrap().init(blade);
			
			if(!blade.applicationConfig().isInit()){
			    blade.loadAppConf(Const.APP_PROPERTIES);
				blade.applicationConfig().setEnv(blade.config());
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
