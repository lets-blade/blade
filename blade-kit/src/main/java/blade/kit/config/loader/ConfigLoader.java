package blade.kit.config.loader;

import blade.kit.StringKit;
import blade.kit.config.Config;
import blade.kit.config.adapter.ConfigAdapter;
import blade.kit.config.adapter.PropConfigAdapter;
import blade.kit.config.exception.LoadException;
import blade.kit.reflect.ReflectKit;

/**
 * 配置加载器，用于加载配置文件
 */
public class ConfigLoader {
	
	public static Config load(String conf){
		return load(conf, PropConfigAdapter.class);
	}
	
	public static Config load(String conf, Class<? extends ConfigAdapter> adapter){
		
		if(StringKit.isEmpty(conf)){
			throw new LoadException("the config file name is null");
		}
		
		if(null == adapter){
			throw new LoadException("the config adapter class is null");
		}
		
		ConfigAdapter configAdapter = ReflectKit.newBean(adapter);
		
		return configAdapter.read(conf);
	}
}
