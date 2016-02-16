package com.blade.jdbc.ds;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

class ConnectionWrapper implements Connection {
	final Connection delegate;
	final BasicDataSourceImpl dataSource;
	long lastAccessTime;
	boolean invalidate;

	ConnectionWrapper(Connection delegate, BasicDataSourceImpl dataSource, long accessTime) {
		this.delegate = delegate;
		this.dataSource = dataSource;
		this.lastAccessTime = accessTime;
	}

	void closeUnderlyingConnection() {
		try {
			delegate.close();
		} catch (SQLException e) {
			// Ignore
		}
	}

	public String nativeSQL(String sql) throws SQLException {
		checkValid();
		try {
			return delegate.nativeSQL(sql);
		} catch (Throwable e) {
			throw handleException(e);
		}
	}

	public void setAutoCommit(boolean autoCommit) throws SQLException {
		checkValid();
		try {
			delegate.setAutoCommit(autoCommit);
		} catch (Throwable e) {
			throw handleException(e);
		}
	}

	public boolean getAutoCommit() throws SQLException {
		checkValid();
		try {
			return delegate.getAutoCommit();
		} catch (Throwable e) {
			throw handleException(e);
		}
	}

	public void commit() throws SQLException {
		checkValid();
		try {
			delegate.commit();
		} catch (Throwable e) {
			throw handleException(e);
		}
	}

	public void rollback() throws SQLException {
		checkValid();
		try {
			delegate.rollback();
		} catch (Throwable e) {
			throw handleException(e);
		}
	}

	public void close() throws SQLException {
		dataSource.releaseConnection(this);
	}

	public boolean isClosed() throws SQLException {
		checkValid();
		try {
			return delegate.isClosed();
		} catch (Throwable e) {
			throw handleException(e);
		}
	}

	public DatabaseMetaData getMetaData() throws SQLException {
		checkValid();
		try {
			return delegate.getMetaData();
		} catch (Throwable e) {
			throw handleException(e);
		}
	}

	public void setReadOnly(boolean readOnly) throws SQLException {
		checkValid();
		try {
			delegate.setReadOnly(readOnly);
		} catch (Throwable e) {
			throw handleException(e);
		}
	}

	public boolean isReadOnly() throws SQLException {
		checkValid();
		try {
			return delegate.isReadOnly();
		} catch (Throwable e) {
			throw handleException(e);
		}
	}

	public void setCatalog(String catalog) throws SQLException {
		checkValid();
		try {
			delegate.setCatalog(catalog);
		} catch (Throwable e) {
			throw handleException(e);
		}
	}

	public String getCatalog() throws SQLException {
		checkValid();
		try {
			return delegate.getCatalog();
		} catch (Throwable e) {
			throw handleException(e);
		}
	}

	public void setTransactionIsolation(int level) throws SQLException {
		checkValid();
		try {
			delegate.setTransactionIsolation(level);
		} catch (Throwable e) {
			throw handleException(e);
		}
	}

	public int getTransactionIsolation() throws SQLException {
		checkValid();
		try {
			return delegate.getTransactionIsolation();
		} catch (Throwable e) {
			throw handleException(e);
		}
	}

	public SQLWarning getWarnings() throws SQLException {
		checkValid();
		try {
			return delegate.getWarnings();
		} catch (Throwable e) {
			throw handleException(e);
		}
	}

	public void clearWarnings() throws SQLException {
		checkValid();
		try {
			delegate.clearWarnings();
		} catch (Throwable e) {
			throw handleException(e);
		}
	}

	public Map<String, Class<?>> getTypeMap() throws SQLException {
		checkValid();
		try {
			return delegate.getTypeMap();
		} catch (Throwable e) {
			throw handleException(e);
		}
	}

	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
		checkValid();
		try {
			delegate.setTypeMap(map);
		} catch (Throwable e) {
			throw handleException(e);
		}
	}

	public void setHoldability(int holdability) throws SQLException {
		checkValid();
		try {
			delegate.setHoldability(holdability);
		} catch (Throwable e) {
			throw handleException(e);
		}
	}

	public int getHoldability() throws SQLException {
		checkValid();
		try {
			return delegate.getHoldability();
		} catch (Throwable e) {
			throw handleException(e);
		}
	}

	public Savepoint setSavepoint() throws SQLException {
		checkValid();
		try {
			return delegate.setSavepoint();
		} catch (Throwable e) {
			throw handleException(e);
		}
	}

	public Savepoint setSavepoint(String name) throws SQLException {
		checkValid();
		try {
			return delegate.setSavepoint(name);
		} catch (Throwable e) {
			throw handleException(e);
		}
	}

	public void rollback(Savepoint savepoint) throws SQLException {
		checkValid();
		try {
			delegate.rollback(savepoint);
		} catch (Throwable e) {
			throw handleException(e);
		}
	}

	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		checkValid();
		try {
			delegate.releaseSavepoint(savepoint);
		} catch (Throwable e) {
			throw handleException(e);
		}
	}

	public Clob createClob() throws SQLException {
		checkValid();
		try {
			return delegate.createClob();
		} catch (Throwable e) {
			throw handleException(e);
		}
	}

	public Blob createBlob() throws SQLException {
		checkValid();
		try {
			return delegate.createBlob();
		} catch (Throwable e) {
			throw handleException(e);
		}
	}

	public NClob createNClob() throws SQLException {
		checkValid();
		try {
			return delegate.createNClob();
		} catch (Throwable e) {
			throw handleException(e);
		}
	}

	public SQLXML createSQLXML() throws SQLException {
		checkValid();
		try {
			return delegate.createSQLXML();
		} catch (Throwable e) {
			throw handleException(e);
		}
	}

	public boolean isValid(int timeout) throws SQLException {
		checkValid();
		try {
			return delegate.isValid(timeout);
		} catch (Throwable e) {
			throw handleException(e);
		}
	}

	public void setClientInfo(String name, String value) throws SQLClientInfoException {
		try {
			delegate.setClientInfo(name, value);
		} catch (Throwable e) {
			throw handleClientInfoException(e);
		}
	}

	public void setClientInfo(Properties properties) throws SQLClientInfoException {
		try {
			delegate.setClientInfo(properties);
		} catch (Throwable e) {
			throw handleClientInfoException(e);
		}
	}

	public String getClientInfo(String name) throws SQLException {
		checkValid();
		try {
			return delegate.getClientInfo(name);
		} catch (Throwable e) {
			throw handleException(e);
		}
	}

	public Properties getClientInfo() throws SQLException {
		checkValid();
		try {
			return delegate.getClientInfo();
		} catch (Throwable e) {
			throw handleException(e);
		}
	}

	public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
		checkValid();
		try {
			return delegate.createArrayOf(typeName, elements);
		} catch (Throwable e) {
			throw handleException(e);
		}
	}

	public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
		checkValid();
		try {
			return delegate.createStruct(typeName, attributes);
		} catch (Throwable e) {
			throw handleException(e);
		}
	}

	public void setSchema(String schema) throws SQLException {
		throw new SQLException("JDK 7 feature unavailable");
	}

	public String getSchema() throws SQLException {
		throw new SQLException("JDK 7 feature unavailable");
	}

	public void abort(Executor executor) throws SQLException {
		throw new SQLException("JDK 7 feature unavailable");
	}

	public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
		throw new SQLException("JDK 7 feature unavailable");
	}

	public int getNetworkTimeout() throws SQLException {
		throw new SQLException("JDK 7 feature unavailable");
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		try {
			return delegate.unwrap(iface);
		} catch (Throwable e) {
			throw handleException(e);
		}
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		try {
			return delegate.isWrapperFor(iface);
		} catch (Throwable e) {
			throw handleException(e);
		}
	}

	SQLException handleException(Throwable e) {
		if (e instanceof SQLException) {
			SQLException se = (SQLException) e;
			invalidate |= isFatalException(se);
			return se;
		}
		invalidate = true;
		return new SQLException(e);
	}

	private SQLClientInfoException handleClientInfoException(Throwable e) {
		invalidate = true;
		if (e instanceof SQLClientInfoException) {
			return (SQLClientInfoException) e;
		}
		throw new IllegalStateException(e);
	}

	private void checkValid() throws SQLException {
		if (invalidate) {
			throw new SQLException("Connection to " + dataSource + " has been invalidated");
		}
	}

	private static boolean isFatalException(SQLException e) {
		// PK constraint is not fatal for connection
		if (e.getErrorCode() == 2627 || e.getErrorCode() == 2601) {
			return false;
		} else if (e.getMessage() != null && e.getMessage().contains("Violation of PRIMARY KEY constraint")) {
			return false;
		}
		return true;
	}

	@Override
	public Statement createStatement() throws SQLException {
		checkValid();
		try {
			return new StatementWrapper(delegate.createStatement(), this);
		} catch (Throwable e) {
			throw handleException(e);
		}
	}

	@Override
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		checkValid();
		try {
			return new StatementWrapper(delegate.prepareStatement(sql), this);
		} catch (Throwable e) {
			throw handleException(e);
		}
	}

	@Override
	public CallableStatement prepareCall(String sql) throws SQLException {
		checkValid();
		try {
			return new StatementWrapper(delegate.prepareCall(sql), this);
		} catch (Throwable e) {
			throw handleException(e);
		}
	}
	
	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
		checkValid();
		try {
			return new StatementWrapper(delegate.createStatement(resultSetType, resultSetConcurrency), this);
		} catch (Throwable e) {
			throw handleException(e);
		}
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
			throws SQLException {
		checkValid();
		try {
			return new StatementWrapper(delegate.prepareStatement(sql, resultSetType, resultSetConcurrency), this);
		} catch (Throwable e) {
			throw handleException(e);
		}
	}

	@Override
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		checkValid();
		try {
			return new StatementWrapper(delegate.prepareCall(sql, resultSetType, resultSetConcurrency), this);
		} catch (Throwable e) {
			throw handleException(e);
		}
	}
	
	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		checkValid();
		try {
			return new StatementWrapper(
					delegate.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability), this);
		} catch (Throwable e) {
			throw handleException(e);
		}
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
			int resultSetHoldability) throws SQLException {
		checkValid();
		try {
			return new StatementWrapper(
					delegate.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability), this);
		} catch (Throwable e) {
			throw handleException(e);
		}
	}

	@Override
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
			int resultSetHoldability) throws SQLException {
		checkValid();
		try {
			return new StatementWrapper(
					delegate.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability), this);
		} catch (Throwable e) {
			throw handleException(e);
		}
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
		checkValid();
		try {
			return new StatementWrapper(delegate.prepareStatement(sql, autoGeneratedKeys), this);
		} catch (Throwable e) {
			throw handleException(e);
		}
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
		checkValid();
		try {
			return new StatementWrapper(delegate.prepareStatement(sql, columnIndexes), this);
		} catch (Throwable e) {
			throw handleException(e);
		}
	}

	@Override
	public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
		checkValid();
		try {
			return new StatementWrapper(delegate.prepareStatement(sql, columnNames), this);
		} catch (Throwable e) {
			throw handleException(e);
		}
	}
}
