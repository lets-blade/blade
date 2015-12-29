package com.blade.plugin.tx;

import java.sql.Connection;

import javax.sql.DataSource;

import blade.kit.log.Logger;

public class TransactionHolder implements TransactionManager {
	
	private static final Logger LOGGER = Logger.getLogger(TransactionHolder.class);
	
	private static DataSource dataSource;
	
	public TransactionHolder(DataSource dataSource) {
		TransactionHolder.dataSource = dataSource;
	}
	
	private static final ThreadLocal<TransactionManager> tranManager = new ThreadLocal<TransactionManager>() {
		protected TransactionManager initialValue() {
			LOGGER.info("TransactionHolder Initialize");
			return new TransactionManagerImpl(dataSource);
		}
	};
	
	@Override
	public void beginTransaction() {
		tranManager.get().beginTransaction();
	}

	@Override
	public void commit() {
		tranManager.get().commit();
	}

	@Override
	public void rollback() {
		tranManager.get().rollback();
	}

	@Override
	public Connection getConnection() {
		return tranManager.get().getConnection();
	}
	
}
