package blade.plugin.sql2o;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import blade.kit.CollectionKit;
import blade.kit.StringKit;
import blade.kit.log.Logger;

public class Condition {

	private static final Logger LOGGER = Logger.getLogger(Condition.class);

	String tableName;

	String pkName;

	// 存储sql
	String sql;

	// 存储排序
	String orderby;

	// 存储操作,增删改查
	DmlType dmlType;

	// 存储设置的参数,insert-update用
	Map<String, Object> params;

	// 存储where条件，select-update-delete用
	Map<String, Object> equalsParams;

	// 大于
	Map<String, Object> greaterParams;
	// 大于等于
	Map<String, Object> greaterThanParams;
	// 小于
	Map<String, Object> lessParams;
	// 小于等于
	Map<String, Object> lessThanParams;
	// like
	Map<String, String> likeParams;
	// in
	Map<String, Object[]> inParams;
	
	enum DmlType {
		SELECT, COUNT, INSERT, UPDATE, DELETE
	}
	
	
	public Condition(String tableName, String pkName) {
		this.tableName = tableName;
		this.pkName = pkName;
	}

	public String getConditionSql() {
		return getConditionSql(null);
	}

	public String getConditionSql(String sql) {

		String sqlEnd = this.sql;
		
		if (null != sql) {
			sqlEnd = sql;
		}
		
		String conditionSql = SqlBuider.create(this, sqlEnd)
							.addEquals()
							.addGreater()
							.addLess()
							.addGreaterThan()
							.addLessThan()
							.addLike()
							.addIn()
							.addAsString();
		
		
		if (conditionSql.lastIndexOf(" and ") != -1) {
			conditionSql = conditionSql.substring(0, conditionSql.length() - 5);
		}
		return conditionSql;
	}

	/**
	 * 过滤mysql、posrgresql、oracle关键字
	 * 
	 * @param field
	 * @return
	 */
	public String filterKeyWord(String field) {
		String[] filters = { "`", "\"" };
		for (String f : filters) {
			if (field.startsWith(f) && field.endsWith(f)) {
				return field.substring(1, field.length() - 1);
			}
		}
		if (field.indexOf(".") != -1) {
			return field.replace(".", "_");
		}
		return field;
	}

	/**
	 * 打印参数列表
	 */
	public void printLog() {
		if (null != equalsParams && equalsParams.size() > 0) {
			LOGGER.debug("execute parameter：" + equalsParams.values());
		}
		if (null != greaterParams && greaterParams.size() > 0) {
			LOGGER.debug("execute parameter：" + greaterParams.values());
		}
		if (null != lessParams && lessParams.size() > 0) {
			LOGGER.debug("execute parameter：" + lessParams.values());
		}
		if (null != greaterThanParams && greaterThanParams.size() > 0) {
			LOGGER.debug("execute parameter：" + greaterThanParams.values());
		}
		if (null != lessThanParams && lessThanParams.size() > 0) {
			LOGGER.debug("execute parameter：" + lessThanParams.values());
		}
		if (null != likeParams && likeParams.size() > 0) {
			LOGGER.debug("execute parameter：" + likeParams.values());
		}
		if (null != inParams && inParams.size() > 0) {
			Set<String> keys = inParams.keySet();
			for (String name : keys) {
				LOGGER.debug("execute parameter："
						+ Arrays.toString(inParams.get(name)));
			}
		}
	}

	/**
	 * 参数全部清空
	 */
	public void clearMap() {
		
		if(null != this.params){
			this.params.clear();
			this.params = null;
		}
		
		if(null != this.equalsParams){
			this.equalsParams.clear();
			this.equalsParams = null;
		}
		
		if(null != this.greaterParams){
			this.greaterParams.clear();
			this.greaterParams = null;
		}
		
		if(null != this.greaterThanParams){
			this.greaterThanParams.clear();
			this.greaterThanParams = null;
		}
		
		if(null != this.lessParams){
			this.lessParams.clear();
			this.lessParams = null;
		}
		
		if(null != this.lessThanParams){
			this.lessThanParams.clear();
			this.lessThanParams = null;
		}
		
		if(null != this.likeParams){
			this.likeParams.clear();
			this.likeParams = null;
		}
		
		if(null != this.inParams){
			this.inParams.clear();
			this.inParams = null;
		}
    	
	}

	public void select() {
		this.sql = "select * from " + this.tableName;
    	this.equalsParams = CollectionKit.newHashMap();
    	this.dmlType = DmlType.SELECT;
	}
	
	/**
	 * 自定义sql返回查询model对象
	 * @param sql
	 */
	public void select(String sql) {
		this.sql = sql;
    	this.equalsParams = CollectionKit.newHashMap();
    	this.dmlType = DmlType.SELECT;
	}
	
	/**
     * @return	返回计算count
     */
    public void count(){
    	this.sql  = "select count(1) from " + this.tableName;
    	this.equalsParams = CollectionKit.newHashMap();
    	this.dmlType = DmlType.COUNT;
    }
    
    /**
     * 自定义sql返回查询model对象
     * 
     * @param sql
     */
    public void count(String sql){
    	this.sql  = sql;
    	this.equalsParams = CollectionKit.newHashMap();
    	this.dmlType = DmlType.COUNT;
    }
    
    public void update(){
    	this.sql = "update " + this.tableName;
    	this.params = CollectionKit.newHashMap();
    	this.equalsParams = CollectionKit.newHashMap();
    	this.dmlType = DmlType.UPDATE;
    }
    
    /**
     * 自定义更新语句
     * @param sql
     */
    public void update(String sql){
    	this.sql = sql;
    	this.params = CollectionKit.newHashMap();
    	this.equalsParams = CollectionKit.newHashMap();
    	this.dmlType = DmlType.UPDATE;
    }
    
    public void insert(){
    	this.sql = "insert into " + this.tableName;
    	this.params = CollectionKit.newHashMap();
    	this.dmlType = DmlType.INSERT;
    }
    
    /**
     * 设置插入sql
     * @param sql
     */
    public void insert(String sql){
    	this.sql = sql;
    	this.params = CollectionKit.newHashMap();
    	this.dmlType = DmlType.INSERT;
    }
    
    public void delete(){
    	this.sql = "delete from " + this.tableName;
    	this.equalsParams = CollectionKit.newHashMap();
    	this.dmlType = DmlType.DELETE;
    }
    
    /**
     * 设置删除sql
     * @param sql
     */
    public void delete(String sql){
    	this.sql = sql;
    	this.equalsParams = CollectionKit.newHashMap();
    	this.dmlType = DmlType.DELETE;
    }
    
    /**
     * 设置参数列表，新增，更新用到
     * 
     * @param name
     * @param value
     */
    public void param(String name, Object value){
    	if(StringKit.isNotBlank(name) && null != value){
    		this.params.put(name, value);
    	}
    }

    /**
     * 设置where参数列表，查询，更新，删除用到
     * 
     * @param name
     * @param value
     */
    public void where(String name, Object value){
    	if(StringKit.isNotBlank(name) && null != value){
    		this.equalsParams.put(name, value);
    	}
    }
    
    /**
     * 大于条件
     * 
     * @param name
     * @param value
     */
    public void greater(String name, Object value){
    	if(StringKit.isNotBlank(name) && null != value){
    		if(null == this.greaterParams){
    			this.greaterParams = CollectionKit.newHashMap();
        	}
    		this.greaterParams.put(name, value);
    	}
    }
    
    /**
     * 大于等于条件
     * 
     * @param name
     * @param value
     */
    public void greaterThan(String name, Object value){
    	if(StringKit.isNotBlank(name) && null != value){
    		if(null == this.greaterThanParams){
    			this.greaterThanParams = CollectionKit.newHashMap();
        	}
    		this.greaterThanParams.put(name, value);
    	}
    }
    
    /**
     * 小于条件
     * 
     * @param name
     * @param value
     * @return
     */
    public void less(String name, Object value){
    	if(StringKit.isNotBlank(name) && null != value){
    		if(null == this.lessParams){
    			this.lessParams = CollectionKit.newHashMap();
        	}
    		this.lessParams.put(name, value);
    	}
    }
    
    /**
     * 小于等于条件
     * 
     * @param name
     * @param value
     */
    public void lessThan(String name, Object value){
    	if(StringKit.isNotBlank(name) && null != value){
    		
    		if(null == this.lessThanParams){
    			this.lessThanParams = CollectionKit.newHashMap();
        	}
    		
    		this.lessThanParams.put(name, value);
    	}
    }
    
    /**
     * like条件
     * 
     * @param name
     * @param value
     */
    public void like(String name, String value){
    	if(StringKit.isNotBlank(name) && StringKit.isNotBlank(value) && !value.equals("%%")){
    		
    		if(null == this.likeParams){
    			this.likeParams = CollectionKit.newHashMap();
        	}
    		this.likeParams.put(name, value);
    	}
    }
    
    /**
     * in条件
     * 
     * @param name
     * @param value
     */
    public void in(String name, Object... values){
    	if(StringKit.isNotBlank(name) && null != values && values.length > 1){
    		if(null == this.inParams){
    			this.inParams = CollectionKit.newHashMap();
        	}
    		this.inParams.put(name, values);
    	}
    }
    
}
