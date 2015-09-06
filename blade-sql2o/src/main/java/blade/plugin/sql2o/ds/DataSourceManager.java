package blade.plugin.sql2o.ds;

import javax.sql.DataSource;

import org.sql2o.Sql2o;

import blade.plugin.sql2o.DBConfig;
import blade.plugin.sql2o.Sql2oPlugin;
import blade.plugin.sql2o.exception.DataSourceException;

/**
 * 数据源连接管理器
 * @author biezhi
 * @version 1.0
 */
public final class DataSourceManager {

	private static final DataSourceManager DATA_SOURCE_MANAGER = new DataSourceManager();
	
	private DataSource dataSource;
	
	private Sql2o sql2o = null;
	
	private DataSourceManager() {
	}
	
	public static DataSourceManager me(){
		return DATA_SOURCE_MANAGER;
	}
	
	public void run(){
		if(null != this.dataSource){
			sql2o = new Sql2o(this.dataSource);
		} else {
			DBConfig dbConfig = Sql2oPlugin.INSTANCE.dbConfig();
			if(null == dbConfig){
				throw new DataSourceException("数据库配置失败");
			}
			try {
				Class.forName(dbConfig.getDriverName());
				sql2o = new Sql2o(dbConfig.getUrl(), dbConfig.getUserName(), dbConfig.getPassWord());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	public synchronized Sql2o getSql2o(){
		return sql2o;
	}
	
	/**
	 * 提供动态注入datasource
	 * @param dataSource_
	 */
	public void setDataSource(DataSource dataSource){
		this.dataSource = dataSource;
		if(null != this.dataSource){
			sql2o = new Sql2o(this.dataSource);
		}
	}
	
	public DataSource getDataSource(){
		return dataSource;
	}
	
}
