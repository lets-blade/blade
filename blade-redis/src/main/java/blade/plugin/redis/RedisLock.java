package blade.plugin.redis;

import java.util.Random;

import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

/**
 * redis lock
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class RedisLock {

    /** 加锁标志 */
    public static final String LOCKED = "TRUE";

    /** 毫秒与毫微秒的换算单位 1毫秒 = 1000000毫微秒 */
    public static final long MILLI_NANO_CONVERSION = 1000 * 1000L;

    /** 默认超时时间（毫秒） */
    public static final long DEFAULT_TIME_OUT = 1000;

    public static final Random RANDOM = new Random();

    /** 锁的超时时间（秒），过期删除 */
    public static final int EXPIRE = 3 * 60;

    private ShardedJedisPool shardedJedisPool;

    private ShardedJedis jedis;

    private String key;

    // 锁状态标志
    private boolean locked = false;

    /**
     * This creates a RedisLock
     */
    public RedisLock(String key, ShardedJedisPool shardedJedisPool) {
        this.key = key + "_lock";
        this.shardedJedisPool = shardedJedisPool;
        this.jedis = this.shardedJedisPool.getResource();
    }

    /**
     * 加锁 应该以： lock(); try { doSomething(); } finally { unlock()； } 的方式调用
     * @param timeout 超时时间
     * @return 成功或失败标志
     */
    public boolean lock(long timeout) {
        long nano = System.nanoTime();
        timeout *= MILLI_NANO_CONVERSION;
        try {
            while ((System.nanoTime() - nano) < timeout) {
                if (this.jedis.setnx(this.key, LOCKED) == 1) {
                    this.jedis.expire(this.key, EXPIRE);
                    this.locked = true;
                    return this.locked;
                }
                // 短暂休眠，避免出现活锁
                Thread.sleep(3, RANDOM.nextInt(500));
            }
        } catch (Exception e) {
            throw new RuntimeException("Locking error", e);
        }
        return false;
    }

    /**
     * 加锁 应该以： lock(); try { doSomething(); } finally { unlock()； } 的方式调用
     * 
     * @param timeout 超时时间
     * @param expire 锁的超时时间（秒），过期删除
     * @return 成功或失败标志
     */
    public boolean lock(long timeout, int expire) {
        long nano = System.nanoTime();
        timeout *= MILLI_NANO_CONVERSION;
        try {
            while ((System.nanoTime() - nano) < timeout) {
                if (this.jedis.setnx(this.key, LOCKED) == 1) {
                    this.jedis.expire(this.key, expire);
                    this.locked = true;
                    return this.locked;
                }
                // 短暂休眠，避免出现活锁
                Thread.sleep(3, RANDOM.nextInt(500));
            }
        } catch (Exception e) {
            throw new RuntimeException("Locking error", e);
        }
        return false;
    }

    /**
     * 加锁 应该以： lock(); try { doSomething(); } finally { unlock()； } 的方式调用
     * 
     * @return 成功或失败标志
     */
    public boolean lock() {
        return lock(DEFAULT_TIME_OUT);
    }

    /**
     * 解锁 无论是否加锁成功，都需要调用unlock 应该以： lock(); try { doSomething(); } finally { unlock()； } 的方式调用
     */
    public void unlock() {
        try {
            if (this.locked) {
                this.jedis.del(this.key);
            }
        } finally {
        	this.shardedJedisPool.returnResourceObject(this.jedis);
//            this.shardedJedisPool.returnResource(this.jedis);
        }
    }
}
