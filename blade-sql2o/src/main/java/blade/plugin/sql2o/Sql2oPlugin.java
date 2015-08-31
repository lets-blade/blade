package blade.plugin.sql2o;

import java.util.Properties;

import javax.sql.DataSource;

import blade.Blade;
import blade.kit.PropertyKit;
import blade.kit.StringKit;
import blade.kit.log.Logger;
import blade.plugin.Plugin;
import blade.plugin.sql2o.ds.DataSourceManager;
import blade.plugin.sql2o.pool.ConnectionPool;
import blade.plugin.sql2o.pool.Constant;
import blade.plugin.sql2o.pool.PoolConfig;

/**
 * sql2o数据库插件
 * 
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public enum Sql2oPlugin implements Plugin {
	
	INSTANCE;
	
	private Logger LOGGER = Logger.getLogger(Sql2oPlugin.class);
	
	private PoolConfig poolConfig;
	
	private boolean openCache;
	
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
		String keepAliveSql = configProperties.getProperty(Constant.KEEPALIVESQL);
		String minConn = configProperties.getProperty(Constant.MIN_CONN);
		String maxConn = configProperties.getProperty(Constant.MAX_CONN);
		String initConn = configProperties.getProperty(Constant.INIT_CONN);
		String maxActiveConn = configProperties.getProperty(Constant.MAX_ACTIVE_CONN);
		String connWaitTime = configProperties.getProperty(Constant.CONN_WAIT_TIME);
		String connTimeOut = configProperties.getProperty(Constant.CONN_TIME_OUT);
		String isCheakPool = configProperties.getProperty(Constant.IS_CHECK_POOL);
		String periodCheck = configProperties.getProperty(Constant.PERIOD_CHECK);
		String lazyCheck = configProperties.getProperty(Constant.LAZY_CHECK);
		String poolName = configProperties.getProperty(Constant.POOL_NAME);
		
		PoolConfig poolConfig = new PoolConfig();
		
		if(StringKit.isNotBlank(drive)){
			poolConfig.setDriverName(drive);
		}
		
		if(StringKit.isNotBlank(url)){
			poolConfig.setUrl(url);
		}
		
		if(StringKit.isNotBlank(username)){
			poolConfig.setUserName(username);
		}
		
		if(StringKit.isNotBlank(password)){
			poolConfig.setPassWord(password);
		}
		
		if(StringKit.isNotBlank(keepAliveSql)){
			poolConfig.setKeepAliveSql(keepAliveSql);
		}
		
		if(StringKit.isNotBlank(minConn) && StringKit.isNumber(minConn.trim())){
			poolConfig.setMinConn(Integer.valueOf(minConn.trim()));
		}
		
		if(StringKit.isNotBlank(maxConn) && StringKit.isNumber(maxConn.trim())){
			poolConfig.setMaxConn(Integer.valueOf(maxConn.trim()));
		}
		
		if(StringKit.isNotBlank(initConn) && StringKit.isNumber(initConn.trim())){
			poolConfig.setInitConn(Integer.valueOf(initConn.trim()));
		}
		
		if(StringKit.isNotBlank(maxActiveConn) && StringKit.isNumber(maxActiveConn.trim())){
			poolConfig.setMaxActiveConn(Integer.valueOf(maxActiveConn.trim()));
		}
		
		if(StringKit.isNotBlank(connTimeOut) && StringKit.isNumber(connTimeOut.trim())){
			poolConfig.setConnTimeOut(Long.valueOf(connTimeOut.trim()));
		}
		
		if(StringKit.isNotBlank(connWaitTime) && StringKit.isNumber(connWaitTime.trim())){
			poolConfig.setConnWaitTime(Long.valueOf(connWaitTime.trim()));
		}
		
		if(StringKit.isNotBlank(isCheakPool)){
			poolConfig.setCheakPool(Boolean.valueOf(isCheakPool.trim()));
		}
		
		if(StringKit.isNotBlank(periodCheck) && StringKit.isNumber(periodCheck.trim())){
			poolConfig.setPeriodCheck(Long.valueOf(periodCheck.trim()));
		}
		
		if(StringKit.isNotBlank(lazyCheck) && StringKit.isNumber(lazyCheck.trim())){
			poolConfig.setInitDelay(Long.valueOf(lazyCheck.trim()));
		}
		
		if(StringKit.isNotBlank(poolName)){
			poolConfig.setPoolName(poolName);
		}
		
		INSTANCE.poolConfig = poolConfig;
		INSTANCE.openCache = Blade.config().isOpenCache();
		
		return INSTANCE;
	}

	/**
	 * 设置数据库配置
	 * 
	 * @param dbConfig	数据库配置
	 */
	public Sql2oPlugin config(PoolConfig poolConfig){
		INSTANCE.poolConfig = poolConfig;
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
		
		PoolConfig poolConfig = new PoolConfig();
		poolConfig.setDriverName(driver);
		poolConfig.setUrl(url);
		poolConfig.setUserName(user);
		poolConfig.setPassWord(pass);
		INSTANCE.poolConfig = poolConfig;
		
		return INSTANCE;
	}
	
	public Sql2oPlugin openCache(){
		INSTANCE.openCache = true;
		return INSTANCE;
	}
	
	public boolean isOpenCache() {
		return INSTANCE.openCache;
	}
	
	public PoolConfig poolConfig(){
		return INSTANCE.poolConfig;
	}
	
	@Override
	public void run() {
		DataSource dataSource = DataSourceManager.getDataSource();
		ConnectionPool connectionPool = DataSourceManager.getConnectionPool();
		
		if(null == dataSource && null == connectionPool){
			LOGGER.error("数据库插件配置失败");
		} else {
			LOGGER.debug("数据库插件配置成功...");
		}
	}

}
