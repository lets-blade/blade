package blade.plugin.sql2o;

import javax.sql.DataSource;

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
	
	private boolean openCache;
	
	private Sql2oPlugin() {
	}
	
	/**
	 * 设置数据库配置
	 * 
	 * @param url
	 * @param driver
	 * @param user
	 * @param pass
	 */
	public Sql2oPlugin config(String url, String driver, String user, String pass){
		
		if(StringKit.isNotEmpty(url) && StringKit.isNotEmpty(driver)
				&& StringKit.isNotEmpty(user) && StringKit.isNotEmpty(pass)){
		
			dbConfig = new DBConfig(driver, url,  user, pass);
		}
		
		return INSTANCE;
	}
	
	public Sql2oPlugin openCache(){
		this.openCache = true;
		return INSTANCE;
	}
	
	public boolean isOpenCache() {
		return openCache;
	}
	
	public DBConfig dbConfig(){
		return dbConfig;
	}
	
	@Override
	public void run() {
		DataSource dataSource = DataSourceManager.getDataSource();
		if(null != dataSource){
			LOGGER.debug("数据库插件配置成功...");
		} else {
			LOGGER.error("数据库插件配置失败");
		}
	}

}
