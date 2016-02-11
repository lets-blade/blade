package blade.kit.config;

import java.util.Map;

/**
 * 配置文件顶层接口，用于获取配置文件的真实数据
 * 
 */
public interface Config {

	/**
	 * 获取一个String类型的值
	 * 
	 * @param key	要获取的key
	 * @return		返回string
	 */
	String getString(String key);
	
	/**
	 * 获取一个Integer类型的值
	 * 
	 * @param key	要获取的key
	 * @return		返回int或者null
	 */
	Integer getInt(String key);
	
	/**
	 * 获取一个Long类型的值
	 * 
	 * @param key	要获取的key
	 * @return		返回long或者null
	 */
	Long getLong(String key);
	
	/**
	 * 获取一个Boolean类型的值
	 * 
	 * @param key	要获取的key
	 * @return		返回boolean或者null
	 */
	Boolean getBoolean(String key);
	
	/**
	 * 获取一个Double类型的值
	 * 
	 * @param key	要获取的key
	 * @return		返回double或者null
	 */
	Double getDouble(String key);
	
	Map<String, String> getConfigMap();
	
	/**
	 * 将获取到的配置文件转换成一个接口
	 * 
	 * @param type		接口类型
	 * @return			返回接口对象
	 */
	<T> T get(Class<T> type);
}