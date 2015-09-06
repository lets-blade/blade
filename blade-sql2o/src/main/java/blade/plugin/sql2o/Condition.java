package blade.plugin.sql2o;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
	
	List<Object> logParams = new LinkedList<Object>();
	
	enum DmlType {
		SELECT, COUNT, INSERT, UPDATE, DELETE
	}
	
	
	public Condition(String tableName, String pkName) {
		this.tableName = tableName;
		this.pkName = pkName;
		this.params = CollectionKit.newHashMap();
		this.equalsParams = CollectionKit.newHashMap();
		this.greaterParams = CollectionKit.newHashMap();
		this.greaterThanParams = CollectionKit.newHashMap();
		this.lessParams = CollectionKit.newHashMap();
		this.lessThanParams = CollectionKit.newHashMap();
		this.likeParams = CollectionKit.newHashMap();
		this.inParams = CollectionKit.newHashMap();
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
		
		if (null != logParams && logParams.size() > 0) {
			LOGGER.debug("execute parameter：" + logParams.toString());
		}
	}

	/**
	 * 参数全部清空
	 */
	public void clearMap() {
		
		if(null != this.params){
			this.params.clear();
		}
		
		if(null != this.equalsParams){
			this.equalsParams.clear();
		}
		
		if(null != this.greaterParams){
			this.greaterParams.clear();
		}
		
		if(null != this.greaterThanParams){
			this.greaterThanParams.clear();
		}
		
		if(null != this.lessParams){
			this.lessParams.clear();
		}
		
		if(null != this.lessThanParams){
			this.lessThanParams.clear();
		}
		
		if(null != this.likeParams){
			this.likeParams.clear();
		}
		
		if(null != this.inParams){
			this.inParams.clear();
		}
    	
		if(null != this.logParams){
			this.logParams.clear();
		}
	}

	public void select() {
		this.sql = "select * from " + this.tableName;
		clearMap();
    	this.equalsParams = CollectionKit.newLinkedHashMap();
    	this.dmlType = DmlType.SELECT;
    	this.orderby = null;
	}
	
	/**
	 * 自定义sql返回查询model对象
	 * @param sql
	 */
	public void select(String sql) {
		this.sql = sql;
		clearMap();
    	this.equalsParams = CollectionKit.newLinkedHashMap();
    	this.dmlType = DmlType.SELECT;
    	this.orderby = null;
	}
	
	/**
     * @return	返回计算count
     */
    public void count(){
    	this.sql  = "select count(1) from " + this.tableName;
    	clearMap();
    	this.equalsParams = CollectionKit.newLinkedHashMap();
    	this.dmlType = DmlType.COUNT;
    	this.orderby = null;
    }
    
    /**
     * 自定义sql返回查询model对象
     * 
     * @param sql
     */
    public void count(String sql){
    	this.sql  = sql;
    	clearMap();
    	this.equalsParams = CollectionKit.newLinkedHashMap();
    	this.dmlType = DmlType.COUNT;
    	this.orderby = null;
    }
    
    public Condition update(){
    	this.sql = "update " + this.tableName;
    	clearMap();
    	this.params = CollectionKit.newLinkedHashMap();
    	this.equalsParams = CollectionKit.newLinkedHashMap();
    	this.dmlType = DmlType.UPDATE;
    	return this;
    }
    
    /**
     * 自定义更新语句
     * @param sql
     */
    public Condition update(String sql){
    	this.sql = sql;
    	clearMap();
    	this.params = CollectionKit.newLinkedHashMap();
    	this.equalsParams = CollectionKit.newLinkedHashMap();
    	this.dmlType = DmlType.UPDATE;
    	return this;
    }
    
    public void insert(){
    	this.sql = "insert into " + this.tableName;
    	clearMap();
    	this.params = CollectionKit.newLinkedHashMap();
    	this.dmlType = DmlType.INSERT;
    }
    
    /**
     * 设置插入sql
     * @param sql
     */
    public void insert(String sql){
    	this.sql = sql;
    	clearMap();
    	this.params = CollectionKit.newLinkedHashMap();
    	this.dmlType = DmlType.INSERT;
    }
    
    public void delete(){
    	this.sql = "delete from " + this.tableName;
    	clearMap();
    	this.equalsParams = CollectionKit.newLinkedHashMap();
    	this.dmlType = DmlType.DELETE;
    }
    
    /**
     * 设置删除sql
     * @param sql
     */
    public void delete(String sql){
    	this.sql = sql;
    	clearMap();
    	this.equalsParams = CollectionKit.newLinkedHashMap();
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
    		this.logParams.add(value);
    	}
    }

    public void where(WhereParam whereParam){
    	if(null != whereParam){
    		if(whereParam.equalsParams.size() > 0){
    			this.equalsParams.putAll(whereParam.equalsParams);
    		}
    		
    		if(whereParam.greaterParams.size() > 0){
    			this.greaterParams.putAll(whereParam.greaterParams);
    		}
    		
    		if(whereParam.greaterThanParams.size() > 0){
    			this.greaterThanParams.putAll(whereParam.greaterThanParams);
    		}
    		
    		if(whereParam.lessParams.size() > 0){
    			this.lessParams.putAll(whereParam.lessParams);
    		}
    		
    		if(whereParam.lessThanParams.size() > 0){
    			this.lessThanParams.putAll(whereParam.lessThanParams);
    		}
    		
    		if(whereParam.likeParams.size() > 0){
    			this.likeParams.putAll(whereParam.likeParams);
    		}
    		
    		if(whereParam.inParams.size() > 0){
    			this.inParams.putAll(whereParam.inParams);
    		}
    	
    		if(whereParam.params.size() > 0){
    			this.params.putAll(whereParam.params);
    		}
    		
    		if(whereParam.logParams.size() > 0){
    			this.logParams.addAll(whereParam.logParams);
    		}
    	}
    }
    
    /**
     * 设置where参数列表，查询，更新，删除用到
     * 
     * @param name
     * @param value
     */
    public void eq(String name, Object value){
    	if(StringKit.isNotBlank(name) && null != value){
    		this.equalsParams.put(name, value);
    		this.logParams.add(value);
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
    			this.greaterParams = CollectionKit.newLinkedHashMap();
        	}
    		this.greaterParams.put(name, value);
    		this.logParams.add(value);
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
    			this.greaterThanParams = CollectionKit.newLinkedHashMap();
        	}
    		this.greaterThanParams.put(name, value);
    		this.logParams.add(value);
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
    			this.lessParams = CollectionKit.newLinkedHashMap();
        	}
    		this.lessParams.put(name, value);
    		this.logParams.add(value);
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
    			this.lessThanParams = CollectionKit.newLinkedHashMap();
        	}
    		this.lessThanParams.put(name, value);
    		this.logParams.add(value);
    	}
    }
    
    /**
     * like条件
     * 
     * @param name
     * @param value
     */
    public void like(String name, String value){
    	
    	if(StringKit.isNotBlank(name) && StringKit.isNotBlank(value) 
    			&& value.indexOf("%null")==-1 && value.indexOf("null%")==-1 && !value.equals("%%")){
    		
    		if(null == this.likeParams){
    			this.likeParams = CollectionKit.newLinkedHashMap();
        	}
    		this.likeParams.put(name, value);
    		this.logParams.add(value);
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
    			this.inParams = CollectionKit.newLinkedHashMap();
        	}
    		this.inParams.put(name, values);
    		this.logParams.add(Arrays.toString(values));
    	}
    }
    
}
