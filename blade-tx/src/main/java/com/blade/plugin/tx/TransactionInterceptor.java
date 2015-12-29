package com.blade.plugin.tx;

import javax.sql.DataSource;

import com.blade.aop.AbstractMethodInterceptor;
import com.blade.aop.annotation.AfterAdvice;
import com.blade.aop.annotation.BeforeAdvice;
import com.blade.aop.intercept.MethodInvocation;

public class TransactionInterceptor extends AbstractMethodInterceptor {
	
	private TransactionHolder local = null;
	
	public TransactionInterceptor(DataSource dataSource) {
		local = new TransactionHolder(dataSource);
	}
	
	@BeforeAdvice(expression = "@com.blade.plugin.tx.annotation.Transactional")
	public void beforeAdvice() {
		local.beginTransaction();
	}
	
	@Override
	public Object invoke(MethodInvocation invocation) {
		try {
			return super.invoke(invocation);
		} catch (Exception e) {
			local.rollback();
			e.printStackTrace();
		} catch (Throwable e) {
			local.rollback();
			e.printStackTrace();
		}
		return null;
	}
	
	@AfterAdvice(expression = "@com.blade.plugin.tx.annotation.Transactional")
	public void afterAdvice() {
		local.commit();
	}
	
}