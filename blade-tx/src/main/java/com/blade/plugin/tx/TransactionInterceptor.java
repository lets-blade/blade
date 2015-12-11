package com.blade.plugin.tx;

import java.sql.SQLException;

import javax.sql.DataSource;

import com.blade.aop.AbstractMethodInterceptor;
import com.blade.aop.annotation.AfterAdvice;
import com.blade.aop.annotation.BeforeAdvice;

public class TransactionInterceptor extends AbstractMethodInterceptor {
	
	private TransactionManage transactionManager;
	
	public TransactionInterceptor(DataSource dataSource) {
		this.transactionManager = new TransactionManage(dataSource);
	}
	
	@BeforeAdvice(expression = "@com.blade.plugin.tx.annotation.Transactional")
	public void beforeAdvice() {
		try {
			transactionManager.start();
		} catch (SQLException e) {
			throw new RuntimeException("begin transaction failure", e);
		}
	}
	
	@AfterAdvice(expression = "@com.blade.plugin.tx.annotation.Transactional")
	public void afterAdvice() {
		try {
			transactionManager.commit();
		} catch (SQLException e) {
			throw new RuntimeException("commit transaction failure", e);
		}
	}
	
}