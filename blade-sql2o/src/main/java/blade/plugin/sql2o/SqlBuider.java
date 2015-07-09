package blade.plugin.sql2o;

import java.util.Set;

import blade.plugin.sql2o.Condition;

/**
 * sql组装器
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class SqlBuider {

	private Condition condition;
	private StringBuffer addSqlBuf;
	private StringBuffer appendSqlBuf;
	
	public static SqlBuider create(Condition condition, String baseSql) {
		return new SqlBuider(condition, baseSql);
	}
	
	private SqlBuider(Condition condition, String baseSql) {
		this.condition = condition;
		this.addSqlBuf = new StringBuffer(baseSql);
		this.appendSqlBuf = new StringBuffer(baseSql);
	}
	
	/***************************APPEND:S********************************/
	public SqlBuider appendEquals(){
		// 基础where equals条件
		if(null != condition.equalsParams && condition.equalsParams.size() > 0){
			if(appendSqlBuf.indexOf("where") == -1){
				appendSqlBuf.append(" where ");
			}
			Set<String> keys = condition.equalsParams.keySet();
			for(String name : keys){
				appendSqlBuf.append(name + "=" + condition.equalsParams.get(name) + " and ");
			}
		}
		return this;
    }
    
    /**
     * 大于条件
     * @param sql
     * @return
     */
    public SqlBuider appendGreater(){
    	if(null != condition.greaterParams && condition.greaterParams.size() > 0){
			if(appendSqlBuf.indexOf("where") == -1){
				appendSqlBuf.append(" where ");
			}
			Set<String> keys = condition.greaterParams.keySet();
			for(String name : keys){
				appendSqlBuf.append(name + " > " + condition.greaterParams.get(name) + " and ");
			}
		}
		return this;
    }
    
    /**
     * 小于条件
     * 
     * @param sql
     * @return
     */
    public SqlBuider appendLess(){
    	if(null != condition.lessParams && condition.lessParams.size() > 0){
			if(appendSqlBuf.indexOf("where") == -1){
				appendSqlBuf.append(" where ");
			}
			Set<String> keys = condition.lessParams.keySet();
			for(String name : keys){
				appendSqlBuf.append(name + " < " + condition.lessParams.get(name) + " and ");
			}
		}
		return this;
    }
    
    /**
     * 大于等于条件
     * @param sql
     * @return
     */
    public SqlBuider appendGreaterThan(){
		if(null != condition.greaterThanParams && condition.greaterThanParams.size() > 0){
			if(appendSqlBuf.indexOf("where") == -1){
				appendSqlBuf.append(" where ");
			}
			Set<String> keys = condition.greaterThanParams.keySet();
			for(String name : keys){
				appendSqlBuf.append(name + " >= " + condition.greaterThanParams.get(name) + " and ");
			}
		}
		return this;
    }
    
    public SqlBuider appendLessThan(){
    	// 小于等于条件
		if(null != condition.lessThanParams && condition.lessThanParams.size() > 0){
			if(appendSqlBuf.indexOf("where") == -1){
				appendSqlBuf.append(" where ");
			}
			Set<String> keys = condition.lessThanParams.keySet();
			for(String name : keys){
				appendSqlBuf.append(name + " <= " + condition.lessThanParams.get(name) + " and ");
			}
		}
		return this;
    }
    
    public SqlBuider appendLike(){
    	// like条件
		if(null != condition.likeParams && condition.likeParams.size() > 0){
			if(appendSqlBuf.indexOf("where") == -1){
				appendSqlBuf.append(" where ");
			}
			Set<String> keys = condition.likeParams.keySet();
			for(String name : keys){
				appendSqlBuf.append(name + " like " + condition.likeParams.get(name) + " and ");
			}
		}
		return this;
    }
    
    public SqlBuider appendIn(){
    	// in条件
		if(null != condition.inParams && condition.inParams.size() > 0){
			if(appendSqlBuf.indexOf("where") == -1){
				appendSqlBuf.append(" where ");
			}
			Set<String> keys = condition.inParams.keySet();
			
			for(String name : keys){
				int len = condition.inParams.get(name).length;
				
				appendSqlBuf.append(name + " in (");
				
				for(int i=0; i<len; i++){
					if(i != len-1){
						appendSqlBuf.append(condition.inParams.get(name)[i] + ", ");
					} else {
						appendSqlBuf.append(condition.inParams.get(name)[i]);
					}
				}
				appendSqlBuf.append(") and ");
			}
		}
		return this;
    }
    
    public String appendAsString(){
    	if(null == appendSqlBuf){
    		return "";
    	}
    	return appendSqlBuf.toString();
    }
    /******************************APPEND:E**********************************/
    

    /******************************A:S**********************************/
    public SqlBuider addEquals(){
		// 基础where equals条件
		if(null != condition.equalsParams && condition.equalsParams.size() > 0){
			if(addSqlBuf.indexOf("where") == -1){
				addSqlBuf.append(" where ");
			}
			Set<String> keys = condition.equalsParams.keySet();
			for(String name : keys){
				addSqlBuf.append(name + " = :" + condition.filterKeyWord(name) + " and ");
			}
		}
		return this;
    }
    
    /**
     * 大于条件
     * @param sql
     * @return
     */
    public SqlBuider addGreater(){
    	if(null != condition.greaterParams && condition.greaterParams.size() > 0){
			if(addSqlBuf.indexOf("where") == -1){
				addSqlBuf.append(" where ");
			}
			Set<String> keys = condition.greaterParams.keySet();
			for(String name : keys){
				addSqlBuf.append(name + " > :" + condition.filterKeyWord(name) + " and ");
			}
		}
		return this;
    }
    
    /**
     * 小于条件
     * 
     * @param sql
     * @return
     */
    public SqlBuider addLess(){
    	if(null != condition.lessParams && condition.lessParams.size() > 0){
			if(addSqlBuf.indexOf("where") == -1){
				addSqlBuf.append(" where ");
			}
			Set<String> keys = condition.lessParams.keySet();
			for(String name : keys){
				addSqlBuf.append(name + " < :" + condition.filterKeyWord(name) + " and ");
			}
		}
		return this;
    }
    
    /**
     * 大于等于条件
     * @param sql
     * @return
     */
    public SqlBuider addGreaterThan(){
		if(null != condition.greaterThanParams && condition.greaterThanParams.size() > 0){
			if(addSqlBuf.indexOf("where") == -1){
				addSqlBuf.append(" where ");
			}
			Set<String> keys = condition.greaterThanParams.keySet();
			for(String name : keys){
				addSqlBuf.append(name + " >= :" + condition.filterKeyWord(name) + " and ");
			}
		}
		return this;
    }
    
    public SqlBuider addLessThan(){
    	// 小于等于条件
		if(null != condition.lessThanParams && condition.lessThanParams.size() > 0){
			if(addSqlBuf.indexOf("where") == -1){
				addSqlBuf.append(" where ");
			}
			Set<String> keys = condition.lessThanParams.keySet();
			for(String name : keys){
				addSqlBuf.append(name + " <= :" + condition.filterKeyWord(name) + " and ");
			}
		}
		return this;
    }
    
    public SqlBuider addLike(){
    	// like条件
		if(null != condition.likeParams && condition.likeParams.size() > 0){
			if(addSqlBuf.indexOf("where") == -1){
				addSqlBuf.append(" where ");
			}
			Set<String> keys = condition.likeParams.keySet();
			for(String name : keys){
				addSqlBuf.append(name + " like :" + condition.filterKeyWord(name) + " and ");
			}
		}
		return this;
    }
    
    public SqlBuider addIn(){
    	// in条件
		if(null != condition.inParams && condition.inParams.size() > 0){
			if(addSqlBuf.indexOf("where") == -1){
				addSqlBuf.append(" where ");
			}
			Set<String> keys = condition.inParams.keySet();
			for (String name : keys) {
				int len = condition.inParams.get(name).length;
				addSqlBuf.append(name + " in (");
				for (int i = 0; i < len; i++) {
					if (i != len - 1) {
						addSqlBuf.append(":" + condition.filterKeyWord(name) + "_" + i
								+ ", ");
					} else {
						addSqlBuf.append(":" + condition.filterKeyWord(name) + "_" + i);
					}
				}
				addSqlBuf.append(") and ");
			}
		}
		return this;
    }
    
    public String addAsString(){
    	if(null == addSqlBuf){
    		return "";
    	}
    	return addSqlBuf.toString();
    }
    
    /******************************A:E**********************************/
}
