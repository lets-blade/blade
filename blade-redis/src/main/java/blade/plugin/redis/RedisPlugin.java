package blade.plugin.redis;

import java.util.ArrayList;
import java.util.List;

import com.blade.plugin.Plugin;

import blade.kit.logging.Logger;
import blade.kit.logging.LoggerFactory;
import redis.clients.jedis.JedisShardInfo;

/**
 * redis插件
 * 
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public enum RedisPlugin implements Plugin {
	
	INSTANCE;
	
	private final Logger LOGGER = LoggerFactory.getLogger(RedisPlugin.class);
	
	private RedisPlugin() {
		redisPoolConfig = new RedisPoolConfig();
		shards = new ArrayList<JedisShardInfo>();
	}
	
	public static RedisPlugin me(){
		return INSTANCE;
	}
	
	/**
	 * redis连接池配置
	 */
	private RedisPoolConfig redisPoolConfig;
	
	/**
	 * 所有redis实例，多台redis主机时可以做集群
	 */
	private List<JedisShardInfo> shards;
	
	public RedisPoolConfig redisPoolConfig() {
		return redisPoolConfig;
	}
	
	public RedisPlugin host(String host, int port) {
		shards.add(new JedisShardInfo(host, port));
		return this;
	}
	
	public RedisPlugin host(String host, int port, String auth) {
		JedisShardInfo jedisShardInfo = new JedisShardInfo(host, port);
		jedisShardInfo.setPassword(auth);
		shards.add(jedisShardInfo);
		return this;
	}
	
	public RedisPlugin host(JedisShardInfo jedisShardInfo) {
		shards.add(jedisShardInfo);
		return this;
	}
	
	public RedisPlugin host(String host) {
		shards.add(new JedisShardInfo(host));
		return this;
	}
	
	public List<JedisShardInfo> shards() {
		return this.shards;
	}
	
	@Override
	public void run() {
		RedisExecutor.init();
		LOGGER.info("redis plugin config success!");
	}

	@Override
	public void destroy() {
		shards.clear();
	}

}
