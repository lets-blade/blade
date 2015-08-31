package blade.plugin.sql2o.pool;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 定义数据库类型
 * @author biezhi
 *
 */
public class DatabaseType {

	public final static int UNKNOW = 0;
	public final static int ORACLE = 1;
	public final static int MYSQL = 2;
	public final static int POSTGRESQL = 3;
	public final static int SQLSERVER = 4;
	public final static int HSQL = 5;
	public final static int DB2 = 6;

	public final static String ORACLE_NAME = "ORACLE";
	public final static String MYSQL_NAME = "MYSQL";
	public final static String POSTGRESQL_NAME = "POSTGRESQL";
	public final static String SQLSERVER_NAME = "SQLSERVER";
	public final static String HSQL_NAME = "HSQL";
	public final static String DB2_NAME = "DB2";

	/**
	 * 获得数据库类型
	 * 
	 * @param conn
	 *            数据库链接
	 * @return int型的数据库类型（如：DatabaseType.ORACLE）
	 */
	public static int getDbType(Connection conn) {

		String dbName = null;
		int dbType = 0;

		try {
			dbName = conn.getMetaData().getDatabaseProductName();
			if (dbName.toUpperCase().indexOf(ORACLE_NAME) > -1) {
				dbType = ORACLE;
			} else if (dbName.toUpperCase().indexOf(MYSQL_NAME) > -1) {
				dbType = MYSQL;
			} else if (dbName.toUpperCase().indexOf(POSTGRESQL_NAME) > -1) {
				dbType = POSTGRESQL;
			} else if (dbName.toUpperCase().indexOf(SQLSERVER_NAME) > -1) {
				dbType = SQLSERVER;
			} else if (dbName.toUpperCase().indexOf(HSQL_NAME) > -1) {
				dbType = HSQL;
			} else if (dbName.toUpperCase().indexOf(DB2_NAME) > -1) {
				dbType = DB2;
			} else {
				dbType = UNKNOW;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return dbType;
	}

	/**
	 * 获得数据库类型
	 * 
	 * @param driverName
	 *            驱动字符串
	 * @return int型的数据库类型（如：DatabaseType.ORACLE）
	 */
	public static int getDbType(String driverName) {

		int dbType = 0;

		if (driverName.toUpperCase().indexOf(ORACLE_NAME) > -1) {
			dbType = ORACLE;
		} else if (driverName.toUpperCase().indexOf(MYSQL_NAME) > -1) {
			dbType = MYSQL;
		} else if (driverName.toUpperCase().indexOf(POSTGRESQL_NAME) > -1) {
			dbType = POSTGRESQL;
		} else if (driverName.toUpperCase().indexOf(SQLSERVER_NAME) > -1) {
			dbType = SQLSERVER;
		} else if (driverName.toUpperCase().indexOf(HSQL_NAME) > -1) {
			dbType = HSQL;
		} else if (driverName.toUpperCase().indexOf(DB2_NAME) > -1) {
			dbType = DB2;
		} else {
			dbType = UNKNOW;
		}

		return dbType;
	}

}
