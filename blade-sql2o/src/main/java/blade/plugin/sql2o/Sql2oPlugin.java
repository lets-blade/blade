package blade.plugin.sql2o;

import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;
import java.util.Properties;

import javax.sql.DataSource;

import com.blade.Blade;
import com.blade.plugin.Plugin;

import blade.cache.CacheException;
import blade.kit.PropertyKit;
import blade.kit.StringKit;
import blade.kit.log.Logger;
import blade.plugin.sql2o.cache.Sql2oCache;
import blade.plugin.sql2o.cache.Sql2oCacheFactory;
import blade.plugin.sql2o.ds.DataSourceManager;

/**
 * sql2o数据库插件
 * 
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class Sql2oPlugin implements Plugin {
	
	private Logger LOGGER = Logger.getLogger(Sql2oPlugin.class);
	
	private DBConfig dbConfig;
	
	private boolean isOpenCache = false;
	
	public Sql2oPlugin() {
	}
	
	/**
	 * 设置数据库配置
	 * 
	 * @param dbConfig	数据库配置
	 */
	public Sql2oPlugin load(String filePath){
		
		Properties configProperties = PropertyKit.getProperty(filePath);
		String drive = configProperties.getProperty(Constant.DRIVE);
		String url = configProperties.getProperty(Constant.URL);
		String username = configProperties.getProperty(Constant.USERNAME);
		String password = configProperties.getProperty(Constant.PASSWORD);
		String openCache = configProperties.getProperty(Constant.OPEN_CACHE);
		
		if(null == dbConfig){
			dbConfig = new DBConfig();
		}
		
		if(StringKit.isNotBlank(drive)){
			dbConfig.setDriverName(drive);
		}
		
		if(StringKit.isNotBlank(url)){
			dbConfig.setUrl(url);
		}
		
		if(StringKit.isNotBlank(username)){
			dbConfig.setUserName(username);
		}
		
		if(StringKit.isNotBlank(password)){
			dbConfig.setPassWord(password);
		}
		
		if(StringKit.isNotBlank(openCache)){
			isOpenCache = Boolean.valueOf(openCache.trim());
		}
		return this;
	}

	/**
	 * 设置数据库配置
	 * 
	 * @param dbConfig	数据库配置
	 */
	public Sql2oPlugin config(DBConfig dbConfig){
		this.dbConfig = dbConfig;
		return this;
	}
	
	/**
	 * 设置数据源
	 * @param dataSource 数据源对象
	 */
	public Sql2oPlugin config(DataSource dataSource){
		String opencache = Blade.me().config().get("blade.db.opencache");
		if(StringKit.isNotBlank(opencache)){
			isOpenCache = Boolean.valueOf(opencache);
		}
		DataSourceManager.me().setDataSource(dataSource);
		return this;
	}
	
	public Sql2oPlugin openCache(){
		isOpenCache = true;
		return this;
	}
	
	/**
	 * 设置数据库配置
	 * 
	 * @param url
	 * @param driver
	 * @param user
	 * @param pass
	 */
	public Sql2oPlugin config(String url, String user, String pass){
		
		return config(null, url, user, pass);
	}
	
	public Sql2oPlugin config(String driver, String url, String user, String pass){
		if(null == dbConfig){
			dbConfig = new DBConfig();
		}
		dbConfig.setDriverName(driver);
		dbConfig.setUrl(url);
		dbConfig.setUserName(user);
		dbConfig.setPassWord(pass);
		return this;
	}
	
	public boolean isOpenCache() {
		return isOpenCache;
	}
	
	public DBConfig dbConfig(){
		return dbConfig;
	}
	
	@Override
	public void run() {
		DataSourceManager.me().run();
		DataSource dataSource = DataSourceManager.me().getDataSource();
		if(null == dataSource){
			LOGGER.error("blade sql2o config fail!");
		} else {
			LOGGER.debug("blade sql2o config success!");
		}
	}

	@Override
	public void destroy() {
		
		LOGGER.debug("sql2o destroy!");
		
		// 卸载数据库驱动
		try {
			Enumeration<Driver> em = DriverManager.getDrivers();
			if(null != em && em.hasMoreElements()){
				DriverManager.deregisterDriver(em.nextElement());
			}
			// 清理缓存处理线程
			if(isOpenCache){
				Sql2oCache sql2oCache = Sql2oCacheFactory.getSql2oCache();
				sql2oCache.destroy();
			}
		} catch (CacheException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public Sql2oPlugin cache(Boolean cache) {
		this.isOpenCache = cache;
		return this;
	}
	
}
