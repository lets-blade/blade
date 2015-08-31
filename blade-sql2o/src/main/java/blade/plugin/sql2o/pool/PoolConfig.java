package blade.plugin.sql2o.pool;

/**
 * 数据库连接池参数
 * @author biezhi
 *
 */
public class PoolConfig {

	/**
	 * 驱动名称
	 */
	private String driverName;
	
	/**
	 * 连接地址
	 */
	private String url;
	
	/**
	 * 用户名
	 */
	private String userName;
	
	/**
	 * 密码
	 */
	private String passWord;
	
	/**
	 * 连接测试语句
	 */
	private String keepAliveSql;
	
	/**
	 * 最小连接数
	 */
	private int minConn = Constant.DEFAULT_MIN_CONN;
	
	/**
	 * 最大连接数
	 */
	private int maxConn = Constant.DEFAULT_MAX_CONN;
	
	/**
	 * 初始化时连接数
	 */
	private int initConn = Constant.DEFAULT_INIT_CONN;
	
	/**
	 * 数据库最大的连接数
	 */
	private int maxActiveConn = Constant.DEFAULT_MAX_ACTIVE_CONN;
	
	/**
	 * 重复去获得连接的频率
	 */
	private long connWaitTime = Constant.DEFAULT_CONN_WAIT_TIME;
	
	/**
	 * 连接超时时间，默认20分钟
	 */
	private long connTimeOut = Constant.DEFAULT_CONN_TIME_OUT;
	
	/**
	 * 是否定时检查连接池
	 */
	private boolean isCheakPool = true;
	
	/**
	 * 检查频率/秒
	 */
	private long periodCheck = Constant.DEFAULT_PERIOD_CHECK;
	
	/**
	 * 延迟多少时间后开始 检查/秒
	 */
	private long initDelay = Constant.DEFAULT_INIT_DELAY;

	private boolean isopenCache = false;
	
	/**
	 * 数据库连接池的名称
	 */
	private String poolName = Constant.DEFAULT_POOL_NAME;

	public PoolConfig() {
		// TODO Auto-generated constructor stub
	}
	
	public String getPoolName() {
		return poolName;
	}

	public void setPoolName(String poolName) {
		this.poolName = poolName;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

	public int getMinConn() {
		return minConn;
	}

	public void setMinConn(int minConn) {
		this.minConn = minConn;
	}

	public int getMaxConn() {
		return maxConn;
	}

	public void setMaxConn(int maxConn) {
		this.maxConn = maxConn;
	}


	public long getConnWaitTime() {
		return connWaitTime;
	}

	public void setConnWaitTime(long connWaitTime) {
		this.connWaitTime = connWaitTime;
	}


	public int getInitConn() {
		return initConn;
	}

	public void setInitConn(int initConn) {
		this.initConn = initConn;
	}

	public int getMaxActiveConn() {
		return maxActiveConn;
	}

	public void setMaxActiveConn(int maxActiveConn) {
		this.maxActiveConn = maxActiveConn;
	}

	public long getConnTimeOut() {
		return connTimeOut;
	}

	public void setConnTimeOut(long connTimeOut) {
		this.connTimeOut = connTimeOut;
	}

	public boolean isCheakPool() {
		return isCheakPool;
	}

	public void setCheakPool(boolean isCheakPool) {
		this.isCheakPool = isCheakPool;
	}

	public long getPeriodCheck() {
		return periodCheck;
	}

	public void setPeriodCheck(long periodCheck) {
		this.periodCheck = periodCheck;
	}

	public long getInitDelay() {
		return initDelay;
	}

	public void setInitDelay(long initDelay) {
		this.initDelay = initDelay;
	}

	public String getKeepAliveSql() {
		return keepAliveSql;
	}

	public void setKeepAliveSql(String keepAliveSql) {
		this.keepAliveSql = keepAliveSql;
	}

	public boolean isIsopenCache() {
		return isopenCache;
	}
	
	public void setIsopenCache(boolean isopenCache) {
		this.isopenCache = isopenCache;
	}

	@Override
	public String toString() {
		return "PoolConfig [maxConn=" + maxConn + ", minConn=" + minConn
				+ ", initConn=" + initConn + ", connTimeOut=" + connTimeOut
				+ ", keepAliveSql=" + keepAliveSql + ",driverName=" + driverName + ", url="
				+ url + ", username=" + userName + ", password=" + passWord + "]";
	}
}
