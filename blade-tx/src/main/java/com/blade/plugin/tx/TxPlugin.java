package com.blade.plugin.tx;

import javax.sql.DataSource;

import blade.kit.Assert;

import com.blade.aop.AopProxy;
import com.blade.plugin.Plugin;

/**
 * 事务插件
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class TxPlugin implements Plugin {
	
	TransactionInterceptor transactionInterceptor = null;
	
	public TxPlugin() {
	}
	
	public TxPlugin dataSource(DataSource dataSource){
		transactionInterceptor = new TransactionInterceptor(dataSource);
		return this;
	}
	
	@Override
	public void run() {
		Assert.notNull(transactionInterceptor);
		AopProxy.addInterceptor(transactionInterceptor);
	}
	
	@Override
	public void destroy() {
		
	}
	
}
