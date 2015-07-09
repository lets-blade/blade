package blade.plugin.sql2o.connection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

public class ConnectionHolder {
	
	private Map<DataSource, Connection> connectionMap = new HashMap<DataSource, Connection>();

	public Connection getConnection(DataSource dataSource) throws SQLException {
		Connection connection = connectionMap.get(dataSource);
		if (connection == null || connection.isClosed()) {
			connection = dataSource.getConnection();
			connectionMap.put(dataSource, connection);
		}

		return connection;
	}

	public void removeConnection(DataSource dataSource) {
		connectionMap.remove(dataSource);
	}
}