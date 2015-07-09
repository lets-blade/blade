package blade.plugin.redis;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class RedisPoolConfig extends GenericObjectPoolConfig {

	public RedisPoolConfig() {
		// defaults to make your life with connection pool easier :)
	    
	    setMinEvictableIdleTimeMillis(60000);
	    setTimeBetweenEvictionRunsMillis(60000);
	    setNumTestsPerEvictionRun(-1);
	    setTestOnBorrow(true);
        setTestOnReturn(true);
        setTestWhileIdle(true);
        setMaxWaitMillis(3000);
        setMaxTotal(10);
        setMaxIdle(5);
	}
	
}
