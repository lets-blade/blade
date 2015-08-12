package blade.plugin.sql2o.cache;


/**
 * 缓存获取工厂
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public final class Sql2oCacheFactory {

	private static Sql2oCache sql2oCache = new SimpleSql2oCache();
	
	public static Sql2oCache getSql2oCache(){
		return sql2oCache;
	}
	
	/**
	 * 设置缓存
	 * 
	 * @param sql2oCache
	 */
	public static void setSql2oCache(Sql2oCache sql2oCache){
		Sql2oCacheFactory.sql2oCache = sql2oCache;
	}
	
}
