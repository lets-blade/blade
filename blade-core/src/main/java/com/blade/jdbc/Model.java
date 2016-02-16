package com.blade.jdbc;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;

import com.blade.jdbc.annotation.Table;


public class Model {

	private static final Sql2o sql2o = DB.sql2o;

	private static final Logger LOGGER = LoggerFactory.getLogger(Model.class);

	private static <T> String getTableName(Class<T> type) {
		return type.getAnnotation(Table.class).value();
	}
	
	private static <T> String getPkName(Class<T> type) {
		return type.getAnnotation(Table.class).PK();
	}
	
	public static <T> List<T> findAll(final Class<T> type) {
		String tbName = getTableName(type);
		String sql = "select * from " + tbName;

		LOGGER.debug("execute sql: {}", sql);
		
		Connection con = sql2o.open();
		List<T> result = con.createQuery(sql).executeAndFetch(type);
		con.close();
		return result;
	}
	
	public static <T> T findById(final Class<T> type, Serializable id) {
		String tbName = getTableName(type);
		String pkName = getPkName(type);
		String sql = "select * from " + tbName + " where " + pkName + " = :" + pkName;
		LOGGER.debug("execute sql: {}", sql);
		
		Connection con = sql2o.open();
		T result = con.createQuery(sql).addParameter(pkName, id).executeAndFetchFirst(type);
		con.close();
		return result;
	}
	
	/**
	 * "where name like ? and age > ? oredr by ?"
	 * @param type
	 * @param condition
	 * @param params
	 * @return
	 */
	public static <T> T find(final Class<T> type, String condition, Object ... args) {
		String tbName = getTableName(type);
		StringBuffer sql = new StringBuffer("select * from ");
		sql.append(tbName).append(" ");
		
		Map<String, Object> params = getConditionSql(sql, condition, args);
		
		LOGGER.debug("execute sql: {}", sql.toString());
		
		Connection con = sql2o.open();
		T result = query(con.createQuery(sql.toString()), params).executeAndFetchFirst(type);
		con.close();
		return result;
	}
	
	public static <T> List<T> findList(final Class<T> type, String condition, Object ... args) {
		
		String tbName = getTableName(type);
		StringBuffer sql = new StringBuffer("select * from ");
		sql.append(tbName).append(" ");
		
		Map<String, Object> params = getConditionSql(sql, condition, args);
		
		LOGGER.debug("execute sql: {}", sql.toString());
		
		Connection con = sql2o.open();
		List<T> result = query(con.createQuery(sql.toString()), params).executeAndFetch(type);
		con.close();
		return result;
	}
	
	public static <T> List<T> findPage(final Class<T> type, int page, int count, String condition, Object ... args) {
		String tbName = getTableName(type);
		StringBuffer sql = new StringBuffer("select * from ");
		sql.append(tbName).append(" ");
		
		Map<String, Object> params = getConditionSql(sql, condition, args);
		
		LOGGER.debug("execute sql: {}", sql.toString());
		
		Connection con = sql2o.open();
		List<T> result = query(con.createQuery(sql.toString()), params)
				.executeAndFetch(type);
		con.close();
		return result;
	}
	
	private static Query query(Query query, Map<String, Object> params){
		Set<String> keys = params.keySet();
		for(String key : keys){
			query.addParameter(key, params.get(key));
		}
		return query;
	}
	
	private static Map<String, Object> getConditionSql(StringBuffer sql, String condition, Object ... params){
		Map<String, Object> maps = new HashMap<String, Object>();
		int pos = condition.indexOf("?");
		for(int i=0; pos != -1; i++){
			maps.put("param_" + i, params[i]);
			condition = condition.replaceFirst("(\\?)", ":param_" + i);
			pos = condition.indexOf("?");
		}
		sql.append(condition);
		return maps;
	}

	public static <T> T insert(T type) {
		
		return type;
	}
}
