package com.blade.jdbc;

import javax.sql.DataSource;

import org.sql2o.Sql2o;

import com.blade.jdbc.ds.BasicDataSourceImpl;

public class DB {
	
	static Sql2o sql2o;
	
	public static void open(DataSource dataSource) {
		try {
			sql2o = new Sql2o(dataSource);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void open(String url, String user, String pass) {
		sql2o = new Sql2o(url, user, pass);
	}
	
	public static void open(String driver, String url, String user, String pass){
		open(driver, url, user, pass, false);
	}
	
	public static void open(String driver, String url, String user, String pass, boolean useDs){
		if(useDs){
			BasicDataSourceImpl dataSource = new BasicDataSourceImpl("blade-jdbc-ds", driver, url, user, pass);
			open(dataSource);
		} else {
			try {
				Class.forName(driver);
				open(url, user, pass);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	
}