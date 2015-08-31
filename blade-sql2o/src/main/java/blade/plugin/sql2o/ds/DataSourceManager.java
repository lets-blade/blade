package blade.plugin.sql2o.ds;

import javax.sql.DataSource;

import org.sql2o.Sql2o;

import blade.ioc.AbstractBeanFactory;
import blade.ioc.SingleBean;
import blade.plugin.sql2o.Sql2oPlugin;
import blade.plugin.sql2o.exception.DataSourceException;
import blade.plugin.sql2o.exception.PoolException;
import blade.plugin.sql2o.pool.ConnectionPool;
import blade.plugin.sql2o.pool.ConnectionPoolManager;
import blade.plugin.sql2o.pool.InitPoolConfig;
import blade.plugin.sql2o.pool.PoolConfig;

/**
 * 数据源连接管理器
 * @author biezhi
 * @version 1.0
 */
public final class DataSourceManager {

	private static DataSource dataSource;
	
	private static ConnectionPool connectionPool;
	
	private static Sql2o sql2o = null;
	
	private static final AbstractBeanFactory beanFactory = new SingleBean();
	
	private DataSourceManager() {
	}
	
	public static void run(){
		Object dsFactoryObj = beanFactory.getBean(AbstractDataSource.class);
		if(null != dsFactoryObj && dsFactoryObj instanceof AbstractDataSource){
			DataSourceManager.dataSource = ((AbstractDataSource) dsFactoryObj).getDataSource();
			if(null == DataSourceManager.dataSource){
				throw new DataSourceException("数据源初始化失败！");
			}
		} else {
			// 内部连接池
			DataSourceManager.connectionPool = getConnectionPool();
		}
		
		if(null != DataSourceManager.dataSource){
			sql2o = new Sql2o(DataSourceManager.dataSource);
		}
		
		if(null != DataSourceManager.connectionPool){
			sql2o = new Sql2o(connectionPool);
		}
	}
	
	public static Sql2o getSql2o(){
		return sql2o;
	}
	
	public static ConnectionPool getConnectionPool() {
		if(null == connectionPool){
			PoolConfig poolConfig = Sql2oPlugin.INSTANCE.poolConfig();
			if(null == poolConfig){
				throw new PoolException("数据库配置失败");
			}
			InitPoolConfig.add(poolConfig);
			return ConnectionPoolManager.me().getPool(poolConfig.getPoolName());
		}
		return connectionPool;
	}

	/**
	 * 提供动态注入datasource
	 * @param dataSource_
	 */
	public static void setDataSource(DataSource dataSource){
		DataSourceManager.dataSource = dataSource;
		if(null != DataSourceManager.dataSource){
			sql2o = new Sql2o(DataSourceManager.dataSource);
		}
	}
	
	public static DataSource getDataSource(){
		return dataSource;
	}
	
}
