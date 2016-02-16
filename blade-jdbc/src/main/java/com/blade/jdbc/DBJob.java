package com.blade.jdbc;

import org.sql2o.Connection;
import org.sql2o.Sql2oException;

import com.blade.jdbc.exception.CallException;

public abstract class DBJob<T> {
	
	protected Connection connection;
	
	public abstract T execute();
	
	public T call(){
		return call(false);
	}
	
	public synchronized T call(boolean dml){
		try {
			connection = DB.sql2o.beginTransaction();
			T t = execute();
			if(dml){
				connection.commit();
			}
			return t;
		} catch (Sql2oException e) {
			if(dml){
				connection.rollback();
			}
			throw new CallException(e.getMessage());
		}  catch (Exception e) {
			if(dml){
				connection.rollback();
			}
			throw new CallException(e);
		} finally {
			if(null != connection){
				connection.close();
			}
		}
	}
	
}
