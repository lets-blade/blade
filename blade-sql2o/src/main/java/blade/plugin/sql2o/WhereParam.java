package blade.plugin.sql2o;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import blade.kit.CollectionKit;
import blade.kit.StringKit;


/**
 * 参数存储对象
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 * @param <T>
 */
public class WhereParam {
	
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
	
	public WhereParam() {
		params = CollectionKit.newHashMap();
		equalsParams = CollectionKit.newHashMap();
		greaterParams = CollectionKit.newHashMap();
		greaterThanParams = CollectionKit.newHashMap();
		lessParams = CollectionKit.newHashMap();
		lessThanParams = CollectionKit.newHashMap();
		likeParams = CollectionKit.newHashMap();
		inParams = CollectionKit.newHashMap();
	}
	
	public static WhereParam me(){
		return new WhereParam();
	}
	
	public WhereParam eq(String field, Object value){
		if(StringKit.isNotBlank(field) && null != value){
			this.equalsParams.put(field, value);
    		this.logParams.add(value);
		}
		return this;
	}
	
	public Object eq(String field){
		if(StringKit.isNotBlank(field)){
			return this.equalsParams.get(field);
		}
		return null;
	}
	
	public String like(String field){
		if(StringKit.isNotBlank(field)){
			return this.likeParams.get(field);
		}
		return null;
	}
	
	public Object less(String field){
		if(StringKit.isNotBlank(field)){
			return this.lessParams.get(field);
		}
		return null;
	}
	
	public Object lessThan(String field){
		if(StringKit.isNotBlank(field)){
			return this.lessThanParams.get(field);
		}
		return null;
	}
	
	public Object greater(String field){
		if(StringKit.isNotBlank(field)){
			return this.greaterParams.get(field);
		}
		return null;
	}
	
	public Object greaterThan(String field){
		if(StringKit.isNotBlank(field)){
			return this.greaterThanParams.get(field);
		}
		return null;
	}
	
	public Object[] in(String field){
		if(StringKit.isNotBlank(field)){
			return this.inParams.get(field);
		}
		return null;
	}
	
	public WhereParam like(String field, String value){
		if(StringKit.isNotBlank(field) && StringKit.isNotBlank(value) 
    			&& value.indexOf("%null")==-1 && value.indexOf("null%")==-1 && !value.equals("%%")){
    		
    		if(null == this.likeParams){
    			this.likeParams = CollectionKit.newLinkedHashMap();
        	}
    		this.likeParams.put(field, value);
    		this.logParams.add(value);
    	}
		return this;
	}
	
	public WhereParam in(String field, Object... values){
		if(StringKit.isNotBlank(field) && null != values && values.length > 0){
    		if(null == this.inParams){
    			this.inParams = CollectionKit.newLinkedHashMap();
        	}
    		this.inParams.put(field, values);
    		this.logParams.add(Arrays.toString(values));
    	}
		return this;
	}
	
	public WhereParam less(String field, Object value){
		if(StringKit.isNotBlank(field) && null != value){
    		if(null == this.lessParams){
    			this.lessParams = CollectionKit.newLinkedHashMap();
        	}
    		this.lessParams.put(field, value);
    		this.logParams.add(value);
    	}
		return this;
	}

	public WhereParam lessThan(String field, Object value){
		if(StringKit.isNotBlank(field) && null != value){
    		
    		if(null == this.lessThanParams){
    			this.lessThanParams = CollectionKit.newLinkedHashMap();
        	}
    		this.lessThanParams.put(field, value);
    		this.logParams.add(value);
    	}
		return this;
	}
	
	public WhereParam greater(String field, Object value){
		if(StringKit.isNotBlank(field) && null != value){
    		if(null == this.greaterParams){
    			this.greaterParams = CollectionKit.newLinkedHashMap();
        	}
    		this.greaterParams.put(field, value);
    		this.logParams.add(value);
    	}
		return this;
	}

	public WhereParam greaterThan(String field, Object value){
		if(StringKit.isNotBlank(field) && null != value){
    		if(null == this.greaterThanParams){
    			this.greaterThanParams = CollectionKit.newLinkedHashMap();
        	}
    		this.greaterThanParams.put(field, value);
    		this.logParams.add(value);
    	}
		return this;
	}
	
	public WhereParam set(String field, Object value){
		if(StringKit.isNotBlank(field) && null != value){
    		this.params.put(field, value);
    		this.logParams.add(value);
    	}
		return this;
	}
}
