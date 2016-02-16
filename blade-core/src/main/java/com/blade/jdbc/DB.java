package com.blade.jdbc;

import javax.sql.DataSource;

import org.sql2o.Sql2o;

public class DB {
	
	static Sql2o sql2o;
	
	public static void open(DataSource dataSource) {
		sql2o = new Sql2o(dataSource);
	}
	
	public static void open(String url, String user, String pass){
		sql2o = new Sql2o(url, user, pass);
	}
	
}