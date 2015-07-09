package blade.plugin.sql2o.ds;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.sql2o.Sql2o;

import blade.ioc.AbstractBeanFactory;
import blade.ioc.SingleBean;
import blade.plugin.sql2o.DBConfig;
import blade.plugin.sql2o.Sql2oPlugin;
import blade.plugin.sql2o.connection.SingleThreadConnectionHolder;
import blade.plugin.sql2o.exception.DataSourceException;

/**
 * 数据源连接管理器
 * @author biezhi
 * @version 1.0
 */
public final class DataSourceManager {

	private static DataSource dataSource;
	
	private static Sql2o sql2o = null;
	
	private static final AbstractBeanFactory beanFactory = new SingleBean();
	
	private DataSourceManager() {
	}
	
	static{
		
		Object dsFactoryObj = beanFactory.getBean(AbstractDataSource.class);
		if(null != dsFactoryObj && dsFactoryObj instanceof AbstractDataSource){
			DataSourceManager.dataSource = ((AbstractDataSource) dsFactoryObj).getDataSource();
			if(null == DataSourceManager.dataSource){
				throw new DataSourceException("数据源初始化失败！");
			}
		} else {
			// jdbc
			DataSourceManager.dataSource = getJdbcDataSource();
		}
		
		if(null != DataSourceManager.dataSource){
			sql2o = new Sql2o(DataSourceManager.dataSource);
		}
	}
	
	public static Sql2o getSql2o(){
		return sql2o;
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
	
	/**
	 * 获取jdbc数据源
	 * @return JdbcDataSource
	 */
	private static DataSource getJdbcDataSource(){
		DBConfig dbConfig = Sql2oPlugin.INSTANCE.dbConfig();
		if(null == dbConfig){
			throw new DataSourceException("没有配置数据库");
		}
		String url = dbConfig.getUrl();
		String driver = dbConfig.getDrive();
		String username = dbConfig.getUser();
		String password = dbConfig.getPassword();
		dataSource = new JdbcDataSource(url, driver, username, password);
		return dataSource;
	}
	
	/**
	 * 获取数据库链接
	 * @return connection对象
	 */
	public static Connection getConnection() {
		try {
			if(null != dataSource){
				return SingleThreadConnectionHolder.getConnection(dataSource);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return null;
	}

}
