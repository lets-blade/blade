package blade.plugin.sql2o;

import java.util.Properties;

import javax.sql.DataSource;

import blade.Blade;
import blade.kit.PropertyKit;
import blade.kit.StringKit;
import blade.kit.log.Logger;
import blade.plugin.Plugin;
import blade.plugin.sql2o.ds.DataSourceManager;

/**
 * sql2o数据库插件
 * 
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public enum Sql2oPlugin implements Plugin {
	
	INSTANCE;
	
	private Logger LOGGER = Logger.getLogger(Sql2oPlugin.class);
	
	private DBConfig dbConfig;
	
	private boolean isOpenCache = false;
	
	private Sql2oPlugin() {
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
		
		if(null == INSTANCE.dbConfig){
			INSTANCE.dbConfig = new DBConfig();
		}
		
		if(StringKit.isNotBlank(drive)){
			INSTANCE.dbConfig.setDriverName(drive);
		}
		
		if(StringKit.isNotBlank(url)){
			INSTANCE.dbConfig.setUrl(url);
		}
		
		if(StringKit.isNotBlank(username)){
			INSTANCE.dbConfig.setUserName(username);
		}
		
		if(StringKit.isNotBlank(password)){
			INSTANCE.dbConfig.setPassWord(password);
		}
		
		if(StringKit.isNotBlank(openCache)){
			isOpenCache = Boolean.valueOf(openCache.trim());
		}
		return INSTANCE;
	}

	/**
	 * 设置数据库配置
	 * 
	 * @param dbConfig	数据库配置
	 */
	public Sql2oPlugin config(DBConfig dbConfig){
		INSTANCE.dbConfig = dbConfig;
		return INSTANCE;
	}
	
	/**
	 * 设置数据源
	 * @param dataSource 数据源对象
	 */
	public Sql2oPlugin config(DataSource dataSource){
		String opencache = Blade.config().get("blade.db.opencache");
		if(StringKit.isNotBlank(opencache)){
			isOpenCache = Boolean.valueOf(opencache);
		}
		DataSourceManager.me().setDataSource(dataSource);
		return INSTANCE;
	}
	
	public Sql2oPlugin openCache(){
		isOpenCache = true;
		return INSTANCE;
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
		return INSTANCE;
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
			LOGGER.error("数据库插件配置失败");
		} else {
			LOGGER.debug("数据库插件配置成功...");
		}
	}

}
