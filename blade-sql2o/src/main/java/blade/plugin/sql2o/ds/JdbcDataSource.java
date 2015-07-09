package blade.plugin.sql2o.ds;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

public class JdbcDataSource implements DataSource {
	
	private String url;
	private String driver;
	private String username;
	private String password;

	public JdbcDataSource(String url, String driver, String username,
			String password) {
		super();
		this.url = url;
		this.driver = driver;
		this.username = username;
		this.password = password;
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return null;
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return 0;
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return null;
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return null;
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return false;
	}

	@Override
	public Connection getConnection() throws SQLException {
		try {
			Class.forName(this.driver) ;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		Connection conn = DriverManager.getConnection(this.url, this.username, this.password) ;
		return conn;
	}

	@Override
	public Connection getConnection(String username, String password)
			throws SQLException {
		return null;
	}

}
