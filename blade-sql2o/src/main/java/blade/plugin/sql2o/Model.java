package blade.plugin.sql2o;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;

import blade.kit.EncrypKit;
import blade.kit.StringKit;
import blade.kit.log.Logger;
import blade.plugin.sql2o.Condition.DmlType;
import blade.plugin.sql2o.cache.Sql2oCache;
import blade.plugin.sql2o.cache.Sql2oCacheFactory;
import blade.plugin.sql2o.ds.DataSourceManager;

/**
 * 实体对象基类
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class Model<T extends Serializable> {
	
	private static final Logger LOGGER = Logger.getLogger(Model.class);
	
	/**
	 * 缓存对象
	 */
    public Sql2oCache sql2oCache = Sql2oCacheFactory.getSql2oCache();
    
    /**
     * sql2o对象，操作数据库
     */
    private Sql2o sql2o = DataSourceManager.me().getSql2o();
    
    /**
     * 当前class实例
     */
    private Class<T> model;
    
    /**
     * 本次查询是否开启缓存
     */
    private boolean cache = false;
    
    /**
     * 是否在单次查询的时候开启缓存，如果开启，则忽略默认不开启缓存
     */
    private boolean queryIsOpen = false;
    
    /**
     * 条件对象
     */
    private Condition condition;
    
    private String CACHE_KEY_LIST;
	private String CACHE_KEY_COUNT;
	private String CACHE_KEY_DETAIL;
	
	public Model(Class<T> clazz) {
		this.model = clazz;
		this.condition = new Condition(table(), pk());
		
		CACHE_KEY_LIST = model.getName() + ":list";
		CACHE_KEY_COUNT = model.getName() + ":count";
		CACHE_KEY_DETAIL = model.getName() + ":detail";
	}
	
    public Sql2o getSql2o(){
    	return sql2o;
    }
    
    /**
     * @return	返回表名称
     */
    public String table(){
    	return model.getAnnotation(Table.class).value();
    }
    
    /**
     * @return	返回表主键
     */
    public String pk(){
    	return model.getAnnotation(Table.class).PK();
    }
    
    /**
     * @return	返回表是否需要缓存
     */
    private boolean isCache(){
    	if(this.queryIsOpen){
    		return true;
    	}
    	return Sql2oPlugin.INSTANCE.isOpenCache() && model.getAnnotation(Table.class).isCache() && this.cache;
    }
    
    /**
     * 设置当前执行方法不缓存
     */
    public Model<T> cache(boolean cache){
    	if(cache){
    		if(null == sql2oCache){
    			sql2oCache = Sql2oCacheFactory.getSql2oCache();
    		}
    		this.queryIsOpen = true;
    	}
    	this.cache = cache;
    	return this;
    }
    
    /**
     * 初始化操作
     * 初始化是否缓存，每次查询都走一次
     */
    private void init(){
    	this.queryIsOpen = false;
    	this.cache = Sql2oPlugin.INSTANCE.isOpenCache();
    	if(null == this.sql2o){
    		this.sql2o = DataSourceManager.me().getSql2o();
    	}
    }
    
    /**
     * @return	返回查询model对象，推荐方式
     */
    public Model<T> select(){
    	init();
    	condition.select();
    	return this;
    }
    
    public boolean isOpenCache() {
		return Sql2oPlugin.INSTANCE.isOpenCache();
	}

	/**
     * 自定义sql返回查询model对象
     * 
     * @param sql	要查询的sql语句
     * @return		返回查询model对象
     */
    public Model<T> select(String sql){
    	init();
    	condition.select(sql);
    	return this;
    }
	
    /**
     * @return	返回计算count
     */
    public Model<T> count(){
    	init();
    	condition.count();
    	return this;
    }
    
    /**
     * 自定义sql返回查询model对象
     * 
     * @param sql	要查询的sql语句
     * @return		返回查询model对象
     */
    public Model<T> count(String sql){
    	init();
    	condition.count(sql);
    	return this;
    }
    
    /**
     * @return	返回更新model对象，推荐方式
     */
    public Model<T> update(){
    	init();
    	condition.update();
    	return this;
    }
    
    /**
     * 返回更新model对象
     * 
     * @param sql	自定义更新语句
     * @return		返回更新model对象
     */
    public Model<T> update(String sql){
    	init();
    	condition.update(sql);
    	return this;
    }
    
    /**
     * @return	返回插入model对象，推荐方式
     */
    public Model<T> insert(){
    	init();
    	condition.insert();
    	return this;
    }
    
    /**
     * 根据自定义sql返回插入model对象
     * @param sql	自定义插入语句
     * @return		返回插入model对象
     */
    public Model<T> insert(String sql){
    	init();
    	condition.insert(sql);
    	return this;
    }
    
    /**
     * @return	返回删除model对象
     */
    public Model<T> delete(){
    	init();
    	condition.delete();
    	return this;
    }
    
    /**
     * 返回自定义删除model对象
     * 
     * @param sql	自定义删除语句
     * @return		返回自定义删除model对象
     */
    public Model<T> delete(String sql){
    	init();
    	condition.delete(sql);
    	return this;
    }
    
    /**
     * 设置参数列表，新增，更新用到
     * 
     * @param name	参数键
     * @param value	参数值
     * @return		返回model对象
     */
    public Model<T> set(String name, Object value){
    	condition.param(name, value);
    	return this;
    }
    
    /**
     * 设置参数列表，新增，更新用到
     * 
     * @param name	参数键
     * @param value	参数值
     * @return		返回model对象
     */
    public Model<T> param(String name, Object value){
    	condition.param(name, value);
    	return this;
    }
    
    /**
     * 设置where参数列表，查询，更新，删除用到
     * 
     * @param name	参数键
     * @param value	参数值
     * @return		返回model对象
     */
    @Deprecated
    public Model<T> where(String name, Object value){
    	condition.eq(name, value);
    	return this;
    }
    
    /**
     * 设置equals参数列表，查询，更新，删除用到
     * 
     * @param name	参数键
     * @param value	参数值
     * @return		返回model对象
     */
    public Model<T> eq(String name, Object value){
    	condition.eq(name, value);
    	return this;
    }
    
    /**
     * 设置where参数列表，查询，更新，删除用到
     * 
     * @param whereParam	保存参数的对象
     * @return				返回model对象
     */
    public Model<T> where(WhereParam whereParam){
    	condition.where(whereParam);
    	return this;
    }
    
    /**
     * 大于
     * @param name
     * @param value
     * @return
     */
    public Model<T> greater(String name, Object value){
    	condition.greater(name, value);
    	return this;
    }
    
    /**
     * 大于等于
     * 
     * @param name	参数键
     * @param value	参数值
     * @return		返回model对象
     */
    public Model<T> greaterThan(String name, Object value){
    	condition.greaterThan(name, value);
    	return this;
    }
    
    /**
     * 小于
     * @param name	参数键
     * @param value	参数值
     * @return		返回model对象
     */
    public Model<T> less(String name, Object value){
    	condition.less(name, value);
    	return this;
    }
    
    /**
     * 小于等于
     * @param name	参数键
     * @param value	参数值
     * @return		返回model对象
     */
    public Model<T> lessThan(String name, Object value){
    	condition.lessThan(name, value);
    	return this;
    }
    
    /**
     * like
     * @param name
     * @param value
     * @return
     */
    public Model<T> like(String name, String value){
    	condition.like(name, value);
    	return this;
    }
    
    /**
     * in
     * @param name
     * @param value
     * @return
     */
    public Model<T> in(String name, Object... values){
    	condition.in(name, values);
    	return this;
    }
    
    /**
     * 设置排序规则
     * 
     * @param orderby	排序字段和排序规则，如：ordernum desc
     * @return			返回model对象
     */
    public Model<T> orderBy(String orderby){
    	condition.orderby = orderby;
    	return this;
    }
    
    ////////////////////E//////////////////////////
    
    private String getCacheCountKey(){
    	String cacheSql = getCacheKey(null);
		if(cacheSql.indexOf("from ") != -1 && cacheSql.indexOf("count(") == -1){
			int start = cacheSql.indexOf("from ") + 5;
			cacheSql = "selectcount(1)from " + cacheSql.substring(start);
		}
		cacheSql = cacheSql.replaceAll("\\s", "");
    	return cacheSql;
    }
    
    private String getCacheKey(String sql){
    	
    	String sqlEnd = condition.sql;
    	if(StringKit.isNotBlank(sql)){
    		sqlEnd = sql;
    	}
    	
    	String cacheSql = SqlBuider.create(condition, sqlEnd)
	    	.appendEquals()
	    	.appendGreater()
	    	.appendGreaterThan()
	    	.appendLess()
	    	.appendLessThan()
	    	.appendLike()
	    	.appendIn().appendAsString();
    	
    	
		if(cacheSql.lastIndexOf(" and ") != -1){
			cacheSql = cacheSql.substring(0, cacheSql.length() - 5);
		}
		
		if(StringKit.isNotBlank(condition.orderby)){
			cacheSql += " order by " + condition.orderby;
		}
		
		cacheSql = cacheSql.replaceAll("\\s", "");
		
		return cacheSql;
    }

    /**
     * 将参数加入到query中
     * @param query
     * @return
     */
    private Query parseParams(Query query){
    	
    	// insert、update参数
    	if(null != condition.params){
			Set<String> keys = condition.params.keySet();
			for(String name : keys){
				query.addParameter(condition.filterKeyWord(name), condition.params.get(name));
			}
		}
    	
    	// 基础equals条件
		if(null != condition.equalsParams){
			Set<String> keys = condition.equalsParams.keySet();
			for(String name : keys){
				query.addParameter(condition.filterKeyWord(name), condition.equalsParams.get(name));
			}
		}
		
		// 大于条件
		if(null != condition.greaterParams){
			Set<String> keys = condition.greaterParams.keySet();
			for(String name : keys){
				query.addParameter(condition.filterKeyWord(name), condition.greaterParams.get(name));
			}
		}
		
		// 小于条件
		if(null != condition.lessParams){
			Set<String> keys = condition.lessParams.keySet();
			for(String name : keys){
				query.addParameter(condition.filterKeyWord(name), condition.lessParams.get(name));
			}
		}
		
		// 大于等于条件
		if(null != condition.greaterThanParams){
			Set<String> keys = condition.greaterThanParams.keySet();
			for(String name : keys){
				query.addParameter(condition.filterKeyWord(name), condition.greaterThanParams.get(name));
			}
		}
		
		// 小于等于条件
		if(null != condition.lessThanParams){
			Set<String> keys = condition.lessThanParams.keySet();
			for(String name : keys){
				query.addParameter(condition.filterKeyWord(name), condition.lessThanParams.get(name));
			}
		}
		
		// like条件
		if(null != condition.likeParams){
			Set<String> keys = condition.likeParams.keySet();
			for(String name : keys){
				query.addParameter(condition.filterKeyWord(name), condition.likeParams.get(name));
			}
		}
		
		// in条件
		if(null != condition.inParams){
			Set<String> keys = condition.inParams.keySet();
			
			for(String name : keys){
				Object[] objects = condition.inParams.get(name);
				int len = objects.length;
				
				for(int i=0; i<len; i++){
					query.addParameter(condition.filterKeyWord(name) + "_" + i, objects[i]);
				}
			}
		}
		
		return query;
    }
    
    /*************************************SELECT:S*****************************************/
    /**
     * @return	返回记录数
     */
    public long fetchCount(){
    	if(condition.dmlType.equals(DmlType.COUNT) || condition.dmlType.equals(DmlType.SELECT)){
    		
    		String field = null;
    		Long count = 0L;
    		
    		// 是否开启缓存查询
    		if(isCache()){
    			
    			String cacheSql = getCacheCountKey();
    			field = EncrypKit.md5(cacheSql);
    			count = sql2oCache.hgetV(CACHE_KEY_COUNT, field);
    			if(null != count){
    				return count;
    			}
    		}
    		
    		String sqlEnd = condition.getConditionSql();
    		
    		if(sqlEnd.indexOf("from ") != -1 && sqlEnd.indexOf("count(") == -1){
    			int start = sqlEnd.indexOf("from ") + 5;
    			sqlEnd = "select count(1) from " + sqlEnd.substring(start);
    		}
    		Connection conn = null;
    		try {
    			conn = sql2o.open();
				Query query = conn.createQuery(sqlEnd);
				
				query = parseParams(query);
				
				LOGGER.debug("execute sql：" + query.toString());
				condition.printLog();
				
				count = query.executeScalar(Long.class);
				
				if(isCache()){
					sql2oCache.hsetV(CACHE_KEY_COUNT, field, count);
				}
				return count;
			} catch (Exception e) {
				LOGGER.error(e);
			} finally{
				if(null != conn){
					conn.close();
				}
			}
    	}
    	
    	condition.clearMap();
    	
    	return 0L;
    }
    
    private Long getPageCount(){
    	if(condition.dmlType.equals(DmlType.COUNT) || condition.dmlType.equals(DmlType.SELECT)){
    		
    		String field = null;
    		Long count = 0L;
    		// 是否开启缓存查询
    		if(isCache()){
    			
    			String cacheSql = getCacheCountKey();
    			field = EncrypKit.md5(cacheSql);
    			count = sql2oCache.hgetV(CACHE_KEY_COUNT, field);
    			if(null != count){
    				return count;
    			}
    		}
    		
    		String sqlEnd = condition.getConditionSql();
    		
    		if(sqlEnd.indexOf("from ") != -1 && sqlEnd.indexOf("count(") == -1){
    			int start = sqlEnd.indexOf("from ") + 5;
    			sqlEnd = "select count(1) from " + sqlEnd.substring(start);
    		}
    		
    		Connection conn = null;
			try {
				conn = sql2o.open();
				Query query = conn.createQuery(sqlEnd);
				
				query = parseParams(query);
				
				LOGGER.debug("execute sql：" + query.toString());
				condition.printLog();
				
				count = query.executeScalar(Long.class);
				// 是否开启缓存查询
				if(isCache()){
					sql2oCache.hsetV(CACHE_KEY_COUNT, field, count);
				}
			} catch (Exception e) {
				LOGGER.error(e);
			} finally{
				if(null != conn){
					conn.close();
				}
			}
    		return count;
    	}
    	return 0L;
    }
    
    /**
     * @return		返回查询一个对象
     */
	public T fetchOne(){
    	
    	if(condition.dmlType.equals(DmlType.SELECT)){
    		
    		String sqlEnd = condition.getConditionSql();
    		
    		T res = null;
    		String field = null;
    		
    		// 是否开启缓存查询
    		if(isCache()){
    			field = EncrypKit.md5(getCacheKey(null));
    			
    			res = sql2oCache.hget(CACHE_KEY_DETAIL, field);
    			if(null != res){
    				return res;
    			}
    		}
    		Connection conn = null;
    		try {
				conn = sql2o.open();
				Query query = conn.createQuery(sqlEnd);
				query = parseParams(query);
				
				LOGGER.debug("execute sql：" + query.toString());
				condition.printLog();
				condition.clearMap();
				
				res = (T) query.executeAndFetchFirst(this.model);
				
				// 重新放入缓存
				if(isCache() && null != res){
					sql2oCache.hset(CACHE_KEY_DETAIL, field, res);
				}
				return res;
			} catch (Exception e) {
				LOGGER.error(e);
			} finally{
				if(null != conn){
					conn.close();
				}
			}
    	}
    	return null;
    }
    
	/**
     * @return		返回查询一个对象
     */
	public Map<String, Object> fetchMap(){
    	
    	if(condition.dmlType.equals(DmlType.SELECT)){
    		
    		String sqlEnd = condition.getConditionSql();
    		
    		Map<String, Object> res = null;
    		String field = null;
    		
    		// 是否开启缓存查询
    		if(isCache()){
    			field = EncrypKit.md5(getCacheKey(null));
    			
    			res = sql2oCache.hgetV(CACHE_KEY_DETAIL, field);
    			if(null != res){
    				return res;
    			}
    		}
    		Connection conn = null;
    		try {
				conn = sql2o.open();
				Query query = conn.createQuery(sqlEnd);
				query = parseParams(query);
				
				LOGGER.debug("execute sql：" + query.toString());
				condition.printLog();
				condition.clearMap();
				
				org.sql2o.data.Table table = query.executeAndFetchTable();
				if(null != table){
					
					List<Map<String, Object>> list = tableAsList(table);
					
					if(null != list && list.size() > 0){
						res = list.get(0);
					}
				}
				
				// 重新放入缓存
				if(isCache() && null != res){
					sql2oCache.hsetV(CACHE_KEY_DETAIL, field, res);
				}
				return res;
			} catch (Exception e) {
				LOGGER.error(e);
			} finally{
				if(null != conn){
					conn.close();
				}
			}
    	}
    	return null;
    }
	
	public T fetchByPk(Serializable pk){
    	T res = null;
    	
    	if(null != pk){
    		
    		this.select().where(this.pk(), pk);
    		
    		String field = null;
    		// 启用缓存
    		if(isCache()){
    			
    			field = EncrypKit.md5(getCacheKey(null));
    			res = sql2oCache.hget(CACHE_KEY_DETAIL, field);
    			if(null != res){
        			return res;
        		}
    		}
    		
    		String sqlEnd = condition.sql + " where " + pk() + " = :pk";
    		Connection conn = null;
    		try {
				conn = sql2o.open();
				Query query = conn.createQuery(sqlEnd).addParameter("pk", pk);
				
				LOGGER.debug("execute sql：" + query.toString());
				condition.printLog();
				condition.clearMap();
				
				res = (T) query.executeAndFetchFirst(this.model);
				
				if(isCache() && null != res){
					sql2oCache.hset(CACHE_KEY_DETAIL, field, res);
				}
			} catch (Exception e) {
				LOGGER.error(e);
			} finally{
				if(null != conn){
					conn.close();
				}
			}
    	}
    	return res;
    }
    
	/**
     * @return		返回查询一个对象
     */
    @SuppressWarnings("unchecked")
	public <M> M fetchColum(){
    	if(condition.dmlType.equals(DmlType.SELECT)){
    		
    		String sqlEnd = condition.getConditionSql();
    		Connection conn = null;
    		try {
				conn = sql2o.open();
				Query query = conn.createQuery(sqlEnd);
				query = parseParams(query);
				
				LOGGER.debug("execute sql：" + query.toString());
				condition.printLog();
				condition.clearMap();
				
				return (M) query.executeScalar();
			} catch (Exception e) {
				LOGGER.error(e);
			} finally{
				if(null != conn){
					conn.close();
				}
			}
    	}
    	return null;
    }
    
    /**
     * @return	查询list数据
     */
    @SuppressWarnings("unchecked")
	public List<T> fetchList(){
    	if(condition.dmlType.equals(DmlType.SELECT)){
    		
    		String field = null;
    		List<T> result = null;
    		
    		// 开启缓存
    		if(isCache()){
    			field = EncrypKit.md5(getCacheKey(null));
    			result = (List<T>) sql2oCache.hgetlist(CACHE_KEY_LIST, field);
    			if(null != result){
    				return result;
    			}
    		}
    		
    		String sqlEnd = condition.getConditionSql();
    		
    		if(null != condition.orderby){
    			sqlEnd += " order by " + condition.orderby;
    		}
    		Connection conn = null;
    		try {
				conn = sql2o.open();
				Query query = conn.createQuery(sqlEnd);
				query = parseParams(query);
				
				LOGGER.debug("execute sql：" + query.toString());
				
				condition.printLog();
				condition.clearMap();
				
				result = (List<T>) query.executeAndFetch(this.model);
				
				if(isCache() && null != result){
					sql2oCache.hsetlist(CACHE_KEY_LIST, field, result);
				}
				return result;
			} catch (Exception e) {
				LOGGER.error(e);
			} finally{
				if(null != conn){
					conn.close();
				}
			}
    	}
    	return null;
    }
    
    
    /**
     * @return	查询list数据
     */
	public <V> List<V> executeAndFetch(Class<V> clazz){
    	if(condition.dmlType.equals(DmlType.SELECT)){
    		
    		String field = null;
    		List<V> result = null;
    		
    		// 开启缓存
    		if(isCache()){
    			field = EncrypKit.md5(getCacheKey(null));
    			result = sql2oCache.hgetlists(CACHE_KEY_LIST, field);
    			if(null != result){
    				return result;
    			}
    		}
    		
    		String sqlEnd = condition.getConditionSql();
    		
    		if(null != condition.orderby){
    			sqlEnd += " order by " + condition.orderby;
    		}
    		Connection conn = null;
    		try {
				conn = sql2o.open();
				Query query = conn.createQuery(sqlEnd);
				query = parseParams(query);
				
				LOGGER.debug("execute sql：" + query.toString());
				
				condition.printLog();
				condition.clearMap();
				
				result = query.executeAndFetch(clazz);
				
				if(isCache() && null != result){
					sql2oCache.hsetlists(CACHE_KEY_LIST, field, result);
				}
				return result;
			} catch (Exception e) {
				LOGGER.error(e);
			} finally{
				if(null != conn){
					conn.close();
				}
			}
    	}
    	return null;
    }
    
	/**
	 * @return	返回一个listmap类型的数据
	 */
	public List<Map<String, Object>> fetchListMap(){
		
		if(condition.dmlType.equals(DmlType.SELECT)){
    		
    		String field = null;
    		List<Map<String, Object>> result = null;
    		
    		// 开启缓存
    		if(isCache()){
    			field = EncrypKit.md5(getCacheKey(null));
    			result = sql2oCache.hgetlistmap(CACHE_KEY_LIST, field);
    			if(null != result){
    				return result;
    			}
    		}
    		
    		String sqlEnd = condition.getConditionSql();
    		
    		if(null != condition.orderby){
    			sqlEnd += " order by " + condition.orderby;
    		}
    		Connection conn = null;
    		try {
				conn = sql2o.open();
				Query query = conn.createQuery(sqlEnd);
				query = parseParams(query);
				
				LOGGER.debug("execute sql：" + query.toString());
				
				condition.printLog();
				condition.clearMap();
				
				result = query.executeAndFetchTable().asList();
				
				if(isCache() && null != result){
					sql2oCache.hsetlistmap(CACHE_KEY_LIST, field, result);
				}
				
				return result;
			} catch (Exception e) {
				LOGGER.error(e);
			} finally{
				if(null != conn){
					conn.close();
				}
			}
    	}
    	return null;
    }
    
	/**
     * 分页检索
     * 
     * @param page		当前页码
     * @param pageSize	每页条数
     * @return			返回分页后的Page<M>对象
     */
	public Page<T> fetchPage(Integer page, Integer pageSize){
    	
    	if(null == page || page < 1){
    		page = 1;
    	}
    	
    	if(null == pageSize || pageSize < 1){
    		pageSize = 1;
    	}
    	
    	Page<T> pageModel = new Page<T>(0, page, pageSize);
    	
    	if(condition.dmlType.equals(DmlType.SELECT) && null != page && null != pageSize && page > 0 && pageSize > 0){
    		
    		// 查询总记录数
    		long totalCount = getPageCount();
    		
    		String field = null;
    		List<T> results = null;
    		
    		// 开启缓存
    		if(isCache()){
    			String cacheSql = getCacheKey(null) + "limit"+ (page - 1) +"," + pageSize;
    			cacheSql = cacheSql.replaceAll("\\s", "");
    			field = EncrypKit.md5(cacheSql);
    			
    			results = sql2oCache.hgetlist(CACHE_KEY_LIST, field);
    			
    			pageModel = new Page<T>(totalCount, page, pageSize);
        		
        		if(null != results && results.size() > 0){
    				pageModel.setResults((List<T>) results);
    				return pageModel;
    			}
        		
    		}
    		
    		String sqlEnd = condition.getConditionSql();
    				
    		if(null != condition.orderby){
    			sqlEnd += " order by " + condition.orderby;
    		}
    		
    		sqlEnd += " limit :page, :pageSize";
    		
    		condition.eq("page", page - 1);
    		condition.eq("pageSize", pageSize);
    		
    		Connection conn = null;
			try {
				// 设置query
				conn = sql2o.open();
				Query query = conn.createQuery(sqlEnd);
				query = parseParams(query);
				
				LOGGER.debug("execute sql：" + query.toString());
				condition.printLog();
				
				results = (List<T>) query.executeAndFetch(this.model);
				if(null != results && results.size() > 0){
					
					if(isCache()){
						sql2oCache.hsetlist(CACHE_KEY_LIST, field, results);
					}
					pageModel.setResults((List<T>) results);
				}
				
				return pageModel;
			} catch (Exception e) {
				LOGGER.error(e);
			} finally{
				if(null != conn){
					conn.close();
				}
			}
    	}
    	
    	condition.clearMap();
    	
    	return pageModel;
    }
    
    /**
     * 分页检索
     * 
     * @param page		当前页码
     * @param pageSize	每页条数
     * @return			返回分页后的Page<M>对象
     */
	public Page<Map<String, Object>> fetchPageMap(Integer page, Integer pageSize){
    	
		if(null == page || page < 1){
    		page = 1;
    	}
    	
    	if(null == pageSize || pageSize < 1){
    		pageSize = 1;
    	}
    	
    	Page<Map<String, Object>> pageMap = new Page<Map<String, Object>>(0, page, pageSize);
    	
    	if(condition.dmlType.equals(DmlType.SELECT) && null != page && null != pageSize && page > 0 && pageSize > 0){
    		
    		// 查询总记录数
    		long totalCount = getPageCount();
    		
    		String field = null;
    		List<Map<String, Object>> results = null;
    		// 开启缓存
    		if(isCache()){
    			String cacheSql = getCacheKey(null) + "limit"+ (page - 1) +"," + pageSize;
    			cacheSql = cacheSql.replaceAll("\\s", "");
    			field = EncrypKit.md5(cacheSql);
    			
    			results = sql2oCache.hgetlistmap(CACHE_KEY_LIST, field);
    			
        		if(null != results && results.size() > 0){
        			pageMap = new Page<Map<String, Object>>(totalCount, page, pageSize);
        			pageMap.setResults(results);
    				return pageMap;
    			}
        		
    		}
    		
    		String sqlEnd = condition.getConditionSql();
    				
    		if(null != condition.orderby){
    			sqlEnd += " order by " + condition.orderby;
    		}
    		
    		sqlEnd += " limit :page, :pageSize";
    		
    		condition.eq("page", page - 1);
    		condition.eq("pageSize", pageSize);
    		Connection conn = null;
			try {
				// 设置query
				conn = sql2o.open();
				Query query = conn.createQuery(sqlEnd);
				query = parseParams(query);
				
				LOGGER.debug("execute sql：" + query.toString());
				condition.printLog();
				
				results = tableAsList(query.executeAndFetchTable());
				
				pageMap = new Page<Map<String, Object>>(totalCount, page, pageSize);
				
				if(null != results && results.size() > 0){
					if(isCache()){
						sql2oCache.hsetlistmap(CACHE_KEY_LIST, field, results);
					}
					pageMap.setResults(results);
				}
				
			} catch (Exception e) {
				LOGGER.error(e);
			} finally{
				if(null != conn){
					conn.close();
				}
			}
			condition.clearMap();
			
    		return pageMap;
    	}
    	
    	return pageMap;
    }
    
    /*************************************SELECT:E*****************************************/
    
    
    /**
     * 执行并提交
     * @return
     */
	public Integer executeAndCommit() {
    	return executeAndCommit(Integer.class);
    }
    
    /**
     * 执行并提交，事务一致
     * 
     * @return	返回主键
     */
    @SuppressWarnings({ "unchecked", "resource" })
	public <V> V executeAndCommit(Class<V> returnType) {
    	V key = null;
    	Query query = null;
    	try {
			// 插入
			if(condition.dmlType.equals(DmlType.INSERT)){
				query = insertCommit(null);
				
				if(isCache()){
					LOGGER.debug("更新缓存：" + model.getName() + " -> count,list");
					sql2oCache.hdel(CACHE_KEY_COUNT);
					sql2oCache.hdel(CACHE_KEY_LIST);
				}
				
				if(null == query){
					LOGGER.error("query is null");
				} else {
					if(null != returnType){
						key = query.executeUpdate().getKey(returnType);
					} else {
						key = (V) query.executeUpdate().getKey();
					}
				}
				
			}
			
			// 更新
			if(condition.dmlType.equals(DmlType.UPDATE)){
				query = updateCommit(null);
				
				if(isCache()){
					LOGGER.debug("更新缓存：" + model.getName() + " -> detail,list");
					sql2oCache.hdel(CACHE_KEY_DETAIL);
					sql2oCache.hdel(CACHE_KEY_LIST);
				}
				
				if(null == query){
					LOGGER.error("query is null");
				} else {
					key = (V) ((Integer) query.executeUpdate().getResult());
				}
			}
			
			// 删除
			if(condition.dmlType.equals(DmlType.DELETE)){
				query = deleteCommit(null);
				
				if(isCache()){
					LOGGER.debug("更新缓存：" + model.getName() + " -> count,list,detail");
					sql2oCache.hdel(CACHE_KEY_COUNT);
					sql2oCache.hdel(CACHE_KEY_LIST);
					sql2oCache.hdel(CACHE_KEY_DETAIL);
				}
				if(null == query){
					LOGGER.error("query is null");
				} else {
					key = (V) Integer.valueOf(query.executeUpdate().getResult());
				}
				
			}
			
			condition.clearMap();
			return key;
		} catch (Exception e) {
			LOGGER.error(e);
		} finally{
			if(null != query){
				query.close();
			}
		}
    	return null;
    }
	
    /**
     * 执行并提交，事务一致
     * 
     * @param connection
     * @return
     */
    public Connection execute(Connection connection){
    	Query query = null;
		try {
			if (null == connection) {
				connection = sql2o.beginTransaction();
			}
			// 插入
			if (condition.dmlType.equals(DmlType.INSERT)) {
				query = insertCommit(connection);
				if(isCache()){
					sql2oCache.hdel(CACHE_KEY_COUNT);
					sql2oCache.hdel(CACHE_KEY_LIST);
				}
			}
			// 更新
			if (condition.dmlType.equals(DmlType.UPDATE)) {
				query = updateCommit(connection);
				if(isCache()){
					sql2oCache.hdel(CACHE_KEY_DETAIL);
					sql2oCache.hdel(CACHE_KEY_LIST);
				}
			}
			// 删除
			if (condition.dmlType.equals(DmlType.DELETE)) {
				query = deleteCommit(connection);
				if(isCache()){
					sql2oCache.hdel(CACHE_KEY_COUNT);
					sql2oCache.hdel(CACHE_KEY_LIST);
					sql2oCache.hdel(CACHE_KEY_DETAIL);
				}
			}
			condition.clearMap();
			if(null != query){
				return query.getConnection();
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
    	return null;
    }
    
    /**
     * @return	删除并返回连接
     */
	private Query deleteCommit(Connection conn) {
    	
		String deleteSql = condition.getConditionSql();
		
		if(null == conn){
			conn = sql2o.open();
		}
		
		Query query = conn.createQuery(deleteSql);
		query = parseParams(query);
		query.executeUpdate();
		
		LOGGER.debug("execute sql：" + query.toString());
		LOGGER.debug("execute parameter：" + condition.equalsParams.values());
		
        return query;
	}

    /**
     * @return	插入并返回连接
     */
	private Query insertCommit(Connection conn){
		
    	String insertSql = condition.sql;
    	
		if(null != condition.params){
			
			StringBuffer paramBuf = new StringBuffer("(");
			StringBuffer valuesBuf = new StringBuffer(" values(");
			
			Set<String> keys = condition.params.keySet();
			for(String name : keys){
				paramBuf.append(name + ", ");
				valuesBuf.append(":" + condition.filterKeyWord(name) + ", ");
				
			}
			
			if(paramBuf.lastIndexOf(", ") != -1 && valuesBuf.lastIndexOf(", ") != -1){
				String start = paramBuf.substring(0, paramBuf.length() - 2);
				String end = valuesBuf.substring(0, valuesBuf.length() - 2);
				
				insertSql = condition.sql + start + ") " + end + ")";
			}
		}
		
		if(null == conn){
			conn = sql2o.open();
		}
		
		Query query = conn.createQuery(insertSql);
		query = parseParams(query);
		LOGGER.debug("execute sql：" + query.toString());
		LOGGER.debug("execute parameter：" + condition.params.values());
		return query;
    }
    
	/**
     * @return	更新并返回连接
     */
	private Query updateCommit(Connection conn){
    	
    	if(null != condition.params){
			
			StringBuffer setBuf = new StringBuffer(" set ");
			StringBuffer whereBuf = new StringBuffer("(");
			
			String setSql = "";
			String whereSql = "";
			
			Set<String> keys = condition.params.keySet();
			for(String name : keys){
				setBuf.append(name + " = :" + condition.filterKeyWord(name) + ", ");
			}
			
			if(setBuf.lastIndexOf(", ") != -1){
				setSql = setBuf.substring(0, setBuf.length() - 2);
			}
			
			whereSql = condition.getConditionSql("");
			
			if(whereBuf.lastIndexOf(", ") != -1){
				whereSql = whereBuf.substring(0, whereBuf.length() - 2);
			}
			
			String updateSql = condition.sql + setSql + whereSql;
			
			updateSql = processUpdateSql(updateSql);
			
			if(null == conn){
				conn = sql2o.open();
			}
			
    		Query query = conn.createQuery(updateSql);
    		query = parseParams(query);
    		
			LOGGER.debug("execute sql：" + query.toString());
    		LOGGER.debug("execute parameter：" + condition.params.values() + condition.equalsParams.values());
    		
    		return query;
		}
		return null;
    }
    
	/**
	 * 处理更新语句
	 */
    private String processUpdateSql(String updateSql) {
    	String[] sqlArr = updateSql.split(" set");
		StringBuffer s = new StringBuffer(sqlArr[0] + " set");
		int len = sqlArr.length;
		for(int i=1; i<len; i++){
			if(i != (len - 1)){
				s.append(sqlArr[i]+",");
			} else {
				s.append(sqlArr[i]);
			}
		}
		return s.toString();
	}

	/**
     * table转list
     * @param table
     * @return
     */
    public List<Map<String, Object>> tableAsList(org.sql2o.data.Table table) {
		if(null != table){
			List<Map<String, Object>> list = table.asList();
			
			List<Map<String, Object>> result = new ArrayList<Map<String,Object>>(list.size());
			if(null != list && list.size() > 0){
				for(Map<String, Object> map : list){
					Map<String, Object> m = getMap(map);
					if(null != m && m.size() > 0){
						result.add(m);
					}
				}
				
				return result;
			}
		}
		return null;
	}
    
    /**
     * map转换
     * @param m
     * @return
     */
    private Map<String, Object> getMap(Map<String, Object> m) {
		// 一个map就是数据库一行记录
		Set<String> names = m.keySet();
		if(null != names && names.size() > 0){
			Map<String, Object> map = new HashMap<String, Object>(names.size());
			for(String name : names){
				map.put(name, m.get(name));
			}
			return map;
		}
		return null;
	}
    
}
