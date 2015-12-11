package com.blade.plugin.tx;

import javax.sql.DataSource;

import com.blade.aop.AopProxy;
import com.blade.plugin.Plugin;

/**
 * 事务插件
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class TxPlugin implements Plugin {
	
	private TransactionInterceptor transactionInterceptor;
	
	public TxPlugin() {
		
	}
	
	public TxPlugin dataSource(DataSource dataSource){
		transactionInterceptor = new TransactionInterceptor(dataSource);
		return this;
	}
	
	@Override
	public void run() {
		if(null == transactionInterceptor){
			throw new RuntimeException("transaction plugin load error，TransactionInterceptor is NULL!");
		} else {
			AopProxy.addInterceptor(transactionInterceptor);
		}
	}
	
	@Override
	public void destroy() {
		
	}
	
}
