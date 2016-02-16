package com.blade.jdbc.ds;

public interface BasicDataSource {
	
	String getUrl();
	
	int activeCount();
	
	int getIdleConnections();
	
	int getMaxConnections();

	long getBorrowTimeout();
	
	long getLockTimeout();
	
}