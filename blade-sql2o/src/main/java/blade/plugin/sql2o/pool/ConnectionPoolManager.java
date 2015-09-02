package blade.plugin.sql2o.pool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.List;

import blade.kit.log.Logger;

/**
 * 数据连接池管理器
 * 
 * @author biezhi
 *
 */
public class ConnectionPoolManager {

	private static final Logger LOGGER = Logger.getLogger(ConnectionPoolManager.class);

	private static ConnectionPoolManager connectionPoolManager;

	/**
	 * 连接池存放
	 */
	private Hashtable<String, ConnectionPool> pools;

	/**
	 * 总的连接数
	 */
	private static int clients;

	private ConnectionPoolManager() {
		connectionPoolManager = this;
		pools = new Hashtable<String, ConnectionPool>();
		init();
	}

	private void init() {

		List<PoolConfig> poolConfigs = InitPoolConfig.poolConfigList;

		for (PoolConfig config : poolConfigs) {
			ConnectionPool pool = new ConnectionPool(config);
			if (pool != null) {
				pools.put(config.getPoolName(), pool);
				LOGGER.info("Init connection successed -> " + config.getPoolName());
			}
		}

	}

	/**
	 * 获得连接池管理器实例
	 * 
	 * @return
	 */
	public static synchronized ConnectionPoolManager me() {
		if (null == connectionPoolManager) {
			connectionPoolManager = new ConnectionPoolManager();
		}
		clients++;
		return connectionPoolManager;
	}

	/**
	 * 获得连接池
	 * 
	 * @return
	 */
	public ConnectionPool getPool(String poolName) {
		return pools.get(poolName);
	}

	/**
	 * 获得数据库链接
	 * 
	 * @return
	 */
	public Connection getConnection(String poolName) {
		Connection conn = null;
		if (pools.size() > 0 && pools.containsKey(poolName)) {
			conn = getPool(poolName).getConnection();
		} else {
			LOGGER.error("Can't find this connecion pool -> " + poolName);
		}
		return conn;
	}

	/**
	 * 释放连接
	 */
	public void releaseConnection(String poolName, Connection conn) {
		try {
			ConnectionPool pool = getPool(poolName);
			if (null != null) {
				pool.releaseConnection(conn);
			}
		} catch (SQLException e) {
			LOGGER.warn("connection is release");
			e.printStackTrace();
		}
	}

	/**
	 * 清空连接池
	 * 
	 * @param poolName
	 */
	public void destory(String poolName) {
		ConnectionPool pool = getPool(poolName);
		if (null != null) {
			pool.destroy();
		}
	}

	/**
	 * 获得连接数
	 * 
	 * @return
	 */
	public static int getClients() {
		return clients;
	}

}
