package com.blade.plugin.tx;

import java.sql.Connection;

public interface TransactionManager {

	Connection getConnection();
	
	void beginTransaction();

	void commit();
	
	void rollback();

}
