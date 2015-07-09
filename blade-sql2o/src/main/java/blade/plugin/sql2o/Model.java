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

import blade.kit.BeanKit;
import blade.kit.StringKit;
import blade.kit.log.Logger;
import blade.plugin.sql2o.Condition.DmlType;
import blade.plugin.sql2o.cache.Sql2oCache;
import blade.plugin.sql2o.cache.Sql2oCacheFactory;
import blade.plugin.sql2o.ds.DataSourceManager;
import blade.plugin.sql2o.kit.MD5;

/**
 * 实体对象基类
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class Model implements Serializable {
	
	private static final long serialVersionUID = -8227936256753441060L;

	private static final Logger LOGGER = Logger.getLogger(Model.class);
	
	/**
	 * 是否开启缓存
	 */
	private static boolean isOpenCache = Sql2oPlugin.INSTANCE.isOpenCache();
	
	/**
	 * 缓存操作
	 */
    private static Sql2oCache<Model> sql2oCache = isOpenCache ? Sql2oCacheFactory.getSql2oCache() : null;
    
    /**
     * sql2o对象，操作数据库
     */
    private Sql2o sql2o = DataSourceManager.getSql2o();
    
    /**
     * 当前class实例
     */
    private Class<? extends Model> model;
    
    /**
     * 条件对象
     */
    private Condition condition;
    
    private final String CACHE_KEY_LIST = this.getClass().getName() + ":list";
	private final String CACHE_KEY_COUNT = this.getClass().getName() + ":count";
	private final String CACHE_KEY_DETAIL = this.getClass().getName() + ":detail";
	
    public static Model getModel(Class<? extends Model> clazz){
    	return new Model(clazz);
    }
    
    public Model() {
    	this.model = this.getClass();
    	this.condition = new Condition(table(), pk());
	}
    
    public Model(Class<? extends Model> clazz) {
		this.model = clazz;
		this.condition = new Condition(table(), pk());
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
    public boolean isCache(){
    	return isOpenCache && model.getAnnotation(Table.class).isCache();
    }
    
    /**
     * @return	返回查询model对象，推荐方式
     */
    public Model select(){
    	condition.select();
    	return this;
    }
    
    
    /**
     * 自定义sql返回查询model对象
     * 
     * @param sql	要查询的sql语句
     * @return		返回查询model对象
     */
    public Model select(String sql){
    	condition.select(sql);
    	return this;
    }
	
    /**
     * @return	返回计算count
     */
    public Model count(){
    	condition.count();
    	return this;
    }
    
    /**
     * 自定义sql返回查询model对象
     * 
     * @param sql	要查询的sql语句
     * @return		返回查询model对象
     */
    public Model count(String sql){
    	condition.count(sql);
    	return this;
    }
    
    /**
     * @return	返回更新model对象，推荐方式
     */
    public Model update(){
    	condition.update();
    	return this;
    }
    
    /**
     * 返回更新model对象
     * 
     * @param sql	自定义更新语句
     * @return		返回更新model对象
     */
    public Model update(String sql){
    	condition.update(sql);
    	return this;
    }
    
    /**
     * @return	返回插入model对象，推荐方式
     */
    public Model insert(){
    	condition.insert();
    	return this;
    }
    
    /**
     * 根据自定义sql返回插入model对象
     * @param sql	自定义插入语句
     * @return		返回插入model对象
     */
    public Model insert(String sql){
    	condition.insert(sql);
    	return this;
    }
    
    /**
     * @return	返回删除model对象
     */
    public Model delete(){
    	condition.delete();
    	return this;
    }
    
    /**
     * 返回自定义删除model对象
     * 
     * @param sql	自定义删除语句
     * @return		返回自定义删除model对象
     */
    public Model delete(String sql){
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
    public Model param(String name, Object value){
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
    public Model where(String name, Object value){
    	condition.where(name, value);
    	return this;
    }
    
    /**
     * 大于
     * @param name
     * @param value
     * @return
     */
    public Model greater(String name, Object value){
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
    public Model greaterThan(String name, Object value){
    	condition.greaterThan(name, value);
    	return this;
    }
    
    /**
     * 小于
     * @param name	参数键
     * @param value	参数值
     * @return		返回model对象
     */
    public Model less(String name, Object value){
    	condition.less(name, value);
    	return this;
    }
    
    /**
     * 小于等于
     * @param name	参数键
     * @param value	参数值
     * @return		返回model对象
     */
    public Model lessThan(String name, Object value){
    	condition.lessThan(name, value);
    	return this;
    }
    
    /**
     * like
     * @param name
     * @param value
     * @return
     */
    public Model like(String name, String value){
    	condition.like(name, value);
    	return this;
    }
    
    /**
     * in
     * @param name
     * @param value
     * @return
     */
    public Model in(String name, Object... values){
    	condition.in(name, values);
    	return this;
    }
    
    /**
     * 设置排序规则
     * 
     * @param orderby	排序字段和排序规则，如：ordernum desc
     * @return			返回model对象
     */
    public Model orderBy(String orderby){
    	condition.orderby = orderby;
    	return this;
    }
    
    ////////////////////E//////////////////////////
    
    private String getCacheCountKey(){
    	String cacheSql = getCacheKey(null);
		if(cacheSql.indexOf("from") != -1 && cacheSql.indexOf("count(") == -1){
			int start = cacheSql.indexOf("from") + 4;
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
		
		if(null != condition.orderby){
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
    	if(null != condition.params && condition.params.size() > 0){
			Set<String> keys = condition.params.keySet();
			for(String name : keys){
				query.addParameter(condition.filterKeyWord(name), condition.params.get(name));
			}
		}
    	
    	// 基础equals条件
		if(null != condition.equalsParams && condition.equalsParams.size() > 0){
			Set<String> keys = condition.equalsParams.keySet();
			for(String name : keys){
				query.addParameter(condition.filterKeyWord(name), condition.equalsParams.get(name));
			}
		}
		
		// 大于条件
		if(null != condition.greaterParams && condition.greaterParams.size() > 0){
			Set<String> keys = condition.greaterParams.keySet();
			for(String name : keys){
				query.addParameter(condition.filterKeyWord(name), condition.greaterParams.get(name));
			}
		}
		
		// 小于条件
		if(null != condition.lessParams && condition.lessParams.size() > 0){
			Set<String> keys = condition.lessParams.keySet();
			for(String name : keys){
				query.addParameter(condition.filterKeyWord(name), condition.lessParams.get(name));
			}
		}
		
		// 大于等于条件
		if(null != condition.greaterThanParams && condition.greaterThanParams.size() > 0){
			Set<String> keys = condition.greaterThanParams.keySet();
			for(String name : keys){
				query.addParameter(condition.filterKeyWord(name), condition.greaterThanParams.get(name));
			}
		}
		
		// 小于等于条件
		if(null != condition.lessThanParams && condition.lessThanParams.size() > 0){
			Set<String> keys = condition.lessThanParams.keySet();
			for(String name : keys){
				query.addParameter(condition.filterKeyWord(name), condition.lessThanParams.get(name));
			}
		}
		
		// like条件
		if(null != condition.likeParams && condition.likeParams.size() > 0){
			Set<String> keys = condition.likeParams.keySet();
			for(String name : keys){
				query.addParameter(condition.filterKeyWord(name), condition.likeParams.get(name));
			}
		}
		
		// in条件
		if(null != condition.inParams && condition.inParams.size() > 0){
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
    			field = MD5.create(cacheSql);
    			count = sql2oCache.hgetV(CACHE_KEY_COUNT, field);
    			if(null != count){
    				return count;
    			}
    		}
    		
    		String sqlEnd = condition.getConditionSql();
    		
    		if(sqlEnd.indexOf("from") != -1 && sqlEnd.indexOf("count(") == -1){
    			int start = sqlEnd.indexOf("from") + 4;
    			sqlEnd = "select count(1) from " + sqlEnd.substring(start);
    		}
    		
    		Connection conn = sql2o.open();
    		Query query = conn.createQuery(sqlEnd);
    		
    		query = parseParams(query);
    		
    		LOGGER.debug("execute sql：" + query.toString());
    		condition.printLog();
    		
    		count = query.executeScalar(Long.class);
    		
    		if(isCache()){
    			sql2oCache.hsetV(CACHE_KEY_COUNT, field, count);
    		}
    		return count;
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
    			field = MD5.create(cacheSql);
    			count = sql2oCache.hgetV(CACHE_KEY_COUNT, field);
    			if(null != count){
    				return count;
    			}
    		}
    		
    		String sqlEnd = condition.getConditionSql();
    		
    		if(sqlEnd.indexOf("from") != -1 && sqlEnd.indexOf("count(") == -1){
    			int start = sqlEnd.indexOf("from") + 4;
    			sqlEnd = "select count(1) from " + sqlEnd.substring(start);
    		}
    		
    		Connection conn = sql2o.open();
    		Query query = conn.createQuery(sqlEnd);
    		
    		query = parseParams(query);
    		
    		LOGGER.debug("execute sql：" + query.toString());
    		condition.printLog();
    		
    		count = query.executeScalar(Long.class);
    		// 是否开启缓存查询
    		if(isCache()){
    			sql2oCache.hsetV(CACHE_KEY_COUNT, field, count);
    		}
    		return count;
    	}
    	return 0L;
    }
    /**
     * @return		返回查询一个对象
     */
    @SuppressWarnings("unchecked")
	public <M extends Model> M fetchOne(){
    	
    	if(condition.dmlType.equals(DmlType.SELECT)){
    		
    		String sqlEnd = condition.getConditionSql();
    		
    		M res = null;
    		String field = null;
    		
    		// 是否开启缓存查询
    		if(isCache()){
    			field = MD5.create(getCacheKey(null));
    			
    			res = sql2oCache.hget(CACHE_KEY_DETAIL, field);
    			if(null != res){
    				return res;
    			}
    		}
    		
    		Connection conn = sql2o.open();
    		Query query = conn.createQuery(sqlEnd);
    		query = parseParams(query);
    		
    		LOGGER.debug("execute sql：" + query.toString());
    		condition.printLog();
    		condition.clearMap();
    		
    		res = (M) query.executeAndFetchFirst(model);
    		
    		// 重新放入缓存
    		if(isCache() && null != res){
    			sql2oCache.hset(CACHE_KEY_DETAIL, field, res);
    		}
    		return res;
    	}
    	return null;
    }
    
    @SuppressWarnings("unchecked")
	public <M extends Model> M fetchByPk(Serializable pk){
    	
    	if(condition.dmlType.equals(DmlType.SELECT) && null != pk){
    		
    		M res = null;
    		String field = null;
    		// 启用缓存
    		if(isCache()){
    			field = MD5.create(getCacheKey(null));
    			res = sql2oCache.hget(CACHE_KEY_DETAIL, field);
    			
    			if(null != res){
        			return res;
        		}
    		}
    		
    		String sqlEnd = condition.sql + " where " + pk() + " = :pk";
    		
    		Connection conn = sql2o.open();
    		Query query = conn.createQuery(sqlEnd).addParameter("pk", pk);
    		
    		LOGGER.debug("execute sql：" + query.toString());
    		condition.printLog();
    		condition.clearMap();
    		
    		res = (M) query.executeAndFetchFirst(model);
    		
    		if(isCache() && null != res){
    			sql2oCache.hset(CACHE_KEY_DETAIL, field, res);
    		}
    		
    		return res;
    	}
    	return null;
    }
    
	/**
     * @return		返回查询一个对象
     */
    @SuppressWarnings("unchecked")
	public <M> M fetchColum(){
    	if(condition.dmlType.equals(DmlType.SELECT)){
    		
    		String sqlEnd = condition.getConditionSql();
    		
    		Connection conn = sql2o.open();
    		Query query = conn.createQuery(sqlEnd);
    		query = parseParams(query);
    		
    		LOGGER.debug("execute sql：" + query.toString());
    		condition.printLog();
    		condition.clearMap();
    		
    		return (M) query.executeScalar();
    	}
    	return null;
    }
    
    /**
     * @return	查询list数据
     */
    @SuppressWarnings("unchecked")
	public <M extends Model> List<M> fetchList(){
    	if(condition.dmlType.equals(DmlType.SELECT)){
    		
    		String field = null;
    		List<M> result = null;
    		
    		// 开启缓存
    		if(isCache()){
    			field = MD5.create(getCacheKey(null));
    			result = sql2oCache.hgetlist(CACHE_KEY_LIST, field);
    			if(null != result){
    				return result;
    			}
    		}
    		
    		String sqlEnd = condition.getConditionSql();
    		
    		if(null != condition.orderby){
    			sqlEnd += " order by " + condition.orderby;
    		}
    		
    		Connection conn = sql2o.open();
    		Query query = conn.createQuery(sqlEnd);
    		query = parseParams(query);
    		
    		LOGGER.debug("execute sql：" + query.toString());
    		
    		condition.printLog();
    		condition.clearMap();
    		
    		result = (List<M>) query.executeAndFetch(model);
    		
    		if(isCache() && null != result){
    			sql2oCache.hsetlist(CACHE_KEY_LIST, field, result);
    		}
    		
    		return result;
    	}
    	return null;
    }
    
	/**
	 * @return	返回一个listmap类型的数据
	 */
	public List<Map<String, Object>> fetchListMap(){
		
    	if(condition.dmlType.equals(DmlType.SELECT)){
    		
    		List<? extends Model> list = fetchList();
    		if(null != list && list.size() > 0){
    			List<Map<String, Object>> result = BeanKit.toListMap(list);
    			return result;
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
    @SuppressWarnings("unchecked")
	public <M extends Model> Page<M> fetchPage(Integer page, Integer pageSize){
    	
    	Page<M> pageModel = new Page<M>(0, page, pageSize);
    	
    	if(condition.dmlType.equals(DmlType.SELECT) && null != page && null != pageSize && page > 0 && pageSize > 0){
    		
    		// 查询总记录数
    		long totalCount = getPageCount();
    		
    		String field = null;
    		List<? extends Model> results = null;
    		
    		// 开启缓存
    		if(isCache()){
    			String cacheSql = getCacheKey(null) + "limit"+ (page - 1) +"," + pageSize;
    			cacheSql = cacheSql.replaceAll("\\s", "");
    			field = MD5.create(cacheSql);
    			
    			results = sql2oCache.hgetlist(CACHE_KEY_LIST, field);
    			
    			pageModel = new Page<M>(totalCount, page, pageSize);
        		
        		if(null != results && results.size() > 0){
    				pageModel.setResults((List<M>) results);
    				return pageModel;
    			}
        		
    		}
    		
    		String sqlEnd = condition.getConditionSql();
    				
    		if(null != condition.orderby){
    			sqlEnd += " order by " + condition.orderby;
    		}
    		
    		sqlEnd += " limit :page, :pageSize";
    		
    		condition.equalsParams.put("page", page - 1);
    		condition.equalsParams.put("pageSize", pageSize);
			
			// 设置query
			Connection conn = sql2o.open();
    		Query query = conn.createQuery(sqlEnd);
    		query = parseParams(query);
    		
    		LOGGER.debug("execute sql：" + query.toString());
    		condition.printLog();
    		
    		results = query.executeAndFetch(this.model);
			if(null != results && results.size() > 0){
				
				if(isCache()){
					sql2oCache.hsetlist(CACHE_KEY_LIST, field, results);
				}
				pageModel.setResults((List<M>) results);
			}
			
    		return pageModel;
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
    	
    	Page<Map<String, Object>> pageMap = new Page<Map<String, Object>>(0, page, pageSize);
    	
    	if(condition.dmlType.equals(DmlType.SELECT) && null != page && null != pageSize && page > 0 && pageSize > 0){
    		
     		Page<Model> pageModel = fetchPage(page, pageSize);
     		
			if(null != pageModel && null != pageModel.getResults()){
				
				pageMap = new Page<Map<String, Object>>(pageModel.getTotalCount(), page, pageSize);
				
				List<Model> list = pageModel.getResults();
				List<Map<String, Object>> result = BeanKit.toListMap(list);
				pageMap.setResults(result);
				
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
	public <T> T executeAndCommit(){
    	return executeAndCommit(null);
    }
    
    /**
     * 执行并提交，事务一致
     * 
     * @return	返回主键
     */
    @SuppressWarnings("unchecked")
	public <T> T executeAndCommit(Class<T> returnType){
    	
    	Query query = null;
    	// 插入
    	if(condition.dmlType.equals(DmlType.INSERT)){
    		query = insertCommit(null);
    		
    		LOGGER.debug("更新缓存：" + model.getName() + " -> count,list");
    		
			sql2oCache.hdel(CACHE_KEY_COUNT);
    		sql2oCache.hdel(CACHE_KEY_LIST);
			
    	}
    	
    	// 更新
    	if(condition.dmlType.equals(DmlType.UPDATE)){
    		query = updateCommit(null);
    		
    		LOGGER.debug("更新缓存：" + model.getName() + " -> detail,list");
    		
    		sql2oCache.hdel(CACHE_KEY_DETAIL);
    		sql2oCache.hdel(CACHE_KEY_LIST);
    	}
    	
    	// 删除
    	if(condition.dmlType.equals(DmlType.DELETE)){
    		query = deleteCommit(null);
    		
    		LOGGER.debug("更新缓存：" + model.getName() + " -> count,list,detail");
    		
    		sql2oCache.hdel(CACHE_KEY_COUNT);
    		sql2oCache.hdel(CACHE_KEY_LIST);
    		sql2oCache.hdel(CACHE_KEY_DETAIL);
    		
    	}
    	
    	condition.clearMap();
    	
    	T key = null;
    	if(null != returnType){
    		key = query.executeUpdate().getKey(returnType);
		} else {
			key = (T) query.executeUpdate().getKey();
		}
		return key;
		
    }
	
    /**
     * 执行并提交，事务一致
     * 
     * @param connection
     * @return
     */
    public Query execute(Connection connection){
    	
    	Query query = null;
    	
    	if(null == connection){
    		connection = sql2o.beginTransaction();
    	}
    	
    	// 插入
    	if(condition.dmlType.equals(DmlType.INSERT)){
    		query = insertCommit(connection);
    	}
    	
    	// 更新
    	if(condition.dmlType.equals(DmlType.UPDATE)){
    		query = updateCommit(connection);
    	}
    	
    	// 删除
    	if(condition.dmlType.equals(DmlType.DELETE)){
    		query = deleteCommit(connection);
    	}
    	
    	condition.clearMap();
    	
    	return query;
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
		
		LOGGER.debug("execute sql：" + query.toString());
		LOGGER.debug("execute parameter：" + condition.equalsParams.values());
		
        return query;
	}

    /**
     * @return	插入并返回连接
     */
	private Query insertCommit(Connection conn){
		
    	String insertSql = condition.sql;
    	
		if(!condition.params.isEmpty() && condition.params.size() > 0){
			
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
    	
    	if(null != condition.params && condition.params.size() > 0){
			
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
			
			if(setSql.length() > 0){
				
				String updateSql = condition.sql + setSql + whereSql;
				
				if(null == conn){
					conn = sql2o.open();
				}
				
	    		Query query = conn.createQuery(updateSql);
	    		query = parseParams(query);
				
				LOGGER.debug("execute sql：" + query.toString());
	    		LOGGER.debug("execute parameter：" + condition.params.values() + condition.equalsParams.values());
	    		
	    		return query;
			}
		}
		return null;
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
