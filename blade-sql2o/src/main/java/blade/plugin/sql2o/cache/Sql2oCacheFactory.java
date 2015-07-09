package blade.plugin.sql2o.cache;

import blade.plugin.sql2o.Model;

/**
 * 缓存获取工厂
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public final class Sql2oCacheFactory {

	private static Sql2oCache<Model> sql2oCache = new SimpleSql2oCache<Model>();
	
	public static Sql2oCache<Model> getSql2oCache(){
		return sql2oCache;
	}
	
	/**
	 * 设置缓存
	 * 
	 * @param sql2oCache
	 */
	public static void setSql2oCache(Sql2oCache<Model> sql2oCache){
		Sql2oCacheFactory.sql2oCache = sql2oCache;
	}
	
}
