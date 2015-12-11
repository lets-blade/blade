package com.blade.plugin.tx;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import blade.kit.log.Logger;

public class TransactionManage {
	
	private Logger logger = Logger.getLogger(TransactionManage.class);
	
	private DataSource dataSource;
	
	public TransactionManage(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public void start() throws SQLException {
		Connection connection = getConnection();
		connection.setAutoCommit(false);
		logger.debug("begin transaction.");
	}

	public final void commit() throws SQLException {
		Connection connection = getConnection();
		connection.commit();
		logger.debug("commit transaction.");
	}

	public final void rollback() {
		Connection connection = null;
		try {
			connection = getConnection();
			connection.rollback();
			logger.debug("rollback transaction.");
		} catch (SQLException e) {
			throw new RuntimeException("Couldn't rollback on connection[" + connection + "].", e);
		}
	}

	public final void close() {
		Connection connection = null;
		try {
			connection = getConnection();
			connection.setAutoCommit(true);
			connection.setReadOnly(false);
			connection.close();
		} catch (SQLException e) {
			throw new RuntimeException("Couldn't close connection[" + connection + "].", e);
		}
	}

	private Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}
	
}