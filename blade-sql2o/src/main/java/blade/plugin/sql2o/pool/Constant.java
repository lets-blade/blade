package blade.plugin.sql2o.pool;

/**
 * 连接池常量
 */
public final class Constant {

	/**
	 * 数据库驱动
	 */
	public static final String DRIVE = "blade.db.drive";
	
	/**
	 * 数据库URL
	 */
	public static final String URL = "blade.db.url";
	
	/**
	 * 用户名
	 */
	public static final String USERNAME = "blade.db.username";
	
	/**
	 * 密码
	 */
	public static final String PASSWORD = "blade.db.password";
	
	/**
	 * 测试SQL语句
	 */
	public static final String KEEPALIVESQL = "blade.db.keepAliveSql";
	
	/**
	 * 最小连接
	 */
	public static final String MIN_CONN = "blade.db.minConn";
	
	/**
	 * 最大连接
	 */
	public static final String MAX_CONN = "blade.db.maxConn";
	
	/**
	 * 初始连接数
	 */
	public static final String INIT_CONN = "blade.db.initConn";
	
	/**
	 * 最大活动连接
	 */
	public static final String MAX_ACTIVE_CONN = "blade.db.maxActiveConn";
	
	/**
	 * 连接等待时间
	 */
	public static final String CONN_WAIT_TIME = "blade.db.connWaitTime";
	
	/**
	 * 连接超时时间
	 */
	public static final String CONN_TIME_OUT = "blade.db.connTimeOut";
	
	/**
	 * 是否检查连接池
	 */
	public static final String IS_CHECK_POOL = "blade.db.isCheakPool";
	
	/**
	 * 检查频率
	 */
	public static final String PERIOD_CHECK = "blade.db.periodCheck";
	
	/**
	 * 初始延时检查
	 */
	public static final String LAZY_CHECK = "blade.db.initDelay";
	
	/**
	 * 连接池名称
	 */
	public static final String POOL_NAME = "blade.db.poolName";
	
	/**
	 * 是否开启缓存
	 */
	public static final String OPEN_CACHE = "blade.db.opencache";

	/**
	 * 默认连接池名称
	 */
	public static final String DEFAULT_POOL_NAME = "blade_sql2o_pool";
	
	/**
	 * 默认初始连接数
	 */
	public static final int DEFAULT_INIT_CONN = 5;
	
	/**
	 * 默认最小连接数
	 */
	public static final int DEFAULT_MIN_CONN = 1;
	
	/**
	 * 默认最大连接数
	 */
	public static final int DEFAULT_MAX_CONN = 20;
	
	/**
	 * 默认最大活动连接数
	 */
	public static final int DEFAULT_MAX_ACTIVE_CONN = 100;
	
	/**
	 * 默认最大连接等待时长
	 */
	public static final long DEFAULT_CONN_WAIT_TIME = 1000;
	
	/**
	 * 默认连接超时时间
	 */
	public static final long DEFAULT_CONN_TIME_OUT = 1200;
	
	/**
	 * 默认检查频率
	 */
	public static final long DEFAULT_PERIOD_CHECK = 3600;
	
	/**
	 * 默认初始化延迟检查时间
	 */
	public static final long DEFAULT_INIT_DELAY = 1200;
	
	
}
