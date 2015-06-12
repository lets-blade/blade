package blade.plugin;

import java.util.Set;

import blade.log.Logger;
import blade.resource.ClassPathClassReader;
import blade.resource.ClassReader;
import blade.resource.JarReaderImpl;

/**
 * 加载所有插件
 * <p>
 * </p>
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public final class PluginApplication {

	private static final Logger LOGGER = Logger.getLogger(PluginApplication.class);
	
	private final static ClassReader classPathReader = new ClassPathClassReader();
	private final static ClassReader jarReader = new JarReaderImpl();
	
	/**
	 * 初始化所有插件，暂时不考虑执行顺序问题
	 */
	public static void init(){
		
		// 扫描blade.plugin包下的所有插件
		Set<Class<?>> pluginList = classPathReader.getClass("blade.plugin", Plugin.class, true);
		
		if(null == pluginList || pluginList.size() == 0){
			pluginList = jarReader.getClass("blade.plugin", Plugin.class, true);
		}
		
		if(null != pluginList && pluginList.size() > 0){
			try {
				for (Class<?> clazz : pluginList) {
					Plugin plugin = (Plugin) clazz.newInstance();
					plugin.execute();
				}
			} catch (InstantiationException e) {
				LOGGER.error("初始化插件失败: " + e.getMessage());
			} catch (IllegalAccessException e) {
				LOGGER.error("初始化插件失败: " + e.getMessage());
			}
		}
	}
	
}
