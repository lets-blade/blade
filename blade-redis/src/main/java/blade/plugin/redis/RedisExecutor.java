package blade.plugin.redis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPipeline;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.jedis.Transaction;
import redis.clients.util.SafeEncoder;
import blade.kit.SerializeKit;

/**
 * Redis的辅助类，负责对内存数据库的所有操作
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class RedisExecutor {

    // 数据源
    private static ShardedJedisPool shardedJedisPool;
    
    private static RedisPoolConfig redisPoolConfig = RedisPlugin.INSTANCE.redisPoolConfig();
    
    static{
    	// 根据配置文件,创建shared池实例
        shardedJedisPool = new ShardedJedisPool(redisPoolConfig, RedisPlugin.INSTANCE.shards());
    }
    
    /**
     * 执行器， 它保证在执行操作之后释放数据源returnResource(jedis)
     */
    abstract class Executor<T> {

        ShardedJedis jedis;

        ShardedJedisPool shardedJedisPool;

        public Executor(ShardedJedisPool shardedJedisPool) {
            this.shardedJedisPool = shardedJedisPool;
            jedis = this.shardedJedisPool.getResource();
        }

        /**
         * 回调
         * 
         * @return 执行结果
         */
        abstract T execute();

        /**
         * 调用{@link #execute()}并返回执行结果 它保证在执行{@link #execute()}之后释放数据源returnResource(jedis)
         * 
         * @return 执行结果
         */
        public T getResult() {
            T result = null;
            try {
                result = execute();
            } catch (Throwable e) {
                if (null != jedis) {
                    shardedJedisPool.returnResourceObject(jedis);
                }
                throw new RuntimeException("Redis execute exception", e);
            } finally {
                if (null != jedis) {
                    shardedJedisPool.returnResourceObject(jedis);
                }
            }
            return result;
        }
    }
    
    /**
     * @return	返回ShardedJedis实例
     */
    public ShardedJedis getShardedJedis(){
    	return RedisExecutor.shardedJedisPool.getResource();
    }
    
    /**
     * 删除模糊匹配的key
     * 
     * @param likeKey 模糊匹配的key
     * @return 删除成功的条数
     */
    public Set<String> getKeyLike(final String likeKey) {
        return new Executor<Set<String>>(shardedJedisPool) {

            @Override
            Set<String> execute() {
                Collection<Jedis> jedisC = jedis.getAllShards();
                Iterator<Jedis> iter = jedisC.iterator();
                Set<String> keys = null;
                while (iter.hasNext()) {
                    Jedis _jedis = iter.next();
                    keys = _jedis.keys(likeKey + "*");
                }
                return keys;
            }
        }.getResult();
    }

    /**
     * 删除模糊匹配的key
     * 
     * @param likeKey 模糊匹配的key
     * @return 删除成功的条数
     */
    public long delKeysLike(final String likeKey) {
        return new Executor<Long>(shardedJedisPool) {

            @Override
            Long execute() {
                Collection<Jedis> jedisC = jedis.getAllShards();
                Iterator<Jedis> iter = jedisC.iterator();
                long count = 0;
                while (iter.hasNext()) {
                    Jedis _jedis = iter.next();
                    Set<String> keys = _jedis.keys(likeKey + "*");
                    count += _jedis.del(keys.toArray(new String[keys.size()]));
                }
                return count;
            }
        }.getResult();
    }

    /**
     * 删除
     * 
     * @param key 匹配的key
     * @return 删除成功的条数
     */
    public Long delKey(final String key) {
        return new Executor<Long>(shardedJedisPool) {

            @Override
            Long execute() {
                return jedis.del(key);
            }
        }.getResult();
    }
    
    /**
     * 排序
     * @param key
     * @return
     */
    public List<String> sort(final String key) {
        return new Executor<List<String>>(shardedJedisPool) {

            @Override
            List<String> execute() {
                return jedis.sort(key);
            }
        }.getResult();
    }

    /**
     * 删除
     * 
     * @param key 匹配的key
     * @return 删除成功的条数
     */
    public Long delKey(final String key, final String... fileds) {
        return new Executor<Long>(shardedJedisPool) {

            @Override
            Long execute() {
                return jedis.hdel(key, fileds);
            }
        }.getResult();
    }

    /**
     * 删除
     * 
     * @param keys 匹配的key的集合
     * @return 删除成功的条数
     */
    public Long delKeys(final String[] keys) {
        return new Executor<Long>(shardedJedisPool) {

            @Override
            Long execute() {
                Collection<Jedis> jedisC = jedis.getAllShards();
                Iterator<Jedis> iter = jedisC.iterator();
                long count = 0;
                while (iter.hasNext()) {
                    Jedis _jedis = iter.next();
                    count += _jedis.del(keys);
                }
                return count;
            }
        }.getResult();
    }

    /**
     * 为给定 key 设置生存时间，当 key 过期时(生存时间为 0 )，它会被自动删除。 在 Redis 中，带有生存时间的 key 被称为『可挥发』(volatile)的。
     * 
     * @param key key
     * @param expire 生命周期，单位为秒
     * @return 1: 设置成功 0: 已经超时或key不存在
     */
    public Long expire(final String key, final int expire) {
        return new Executor<Long>(shardedJedisPool) {

            @Override
            Long execute() {
                return jedis.expire(key, expire);
            }
        }.getResult();
    }

    /**
     * 一个跨jvm的id生成器，利用了redis原子性操作的特点
     * 
     * @param key id的key
     * @return 返回生成的Id
     */
    public long makeId(final String key) {
        return new Executor<Long>(shardedJedisPool) {

            @Override
            Long execute() {
                long id = jedis.incr(key);
                if ((id + 75807) >= Long.MAX_VALUE) {
                    // 避免溢出，重置，getSet命令之前允许incr插队，75807就是预留的插队空间
                    jedis.getSet(key, "0");
                }
                return id;
            }
        }.getResult();
    }

    public boolean exists(final String key) {
        return new Executor<Boolean>(shardedJedisPool) {

            @Override
            Boolean execute() {
                return jedis.exists(key);
            }
        }.getResult();
    }

    public boolean exists(final String key, final String field) {
        return new Executor<Boolean>(shardedJedisPool) {

            @Override
            Boolean execute() {
                return jedis.hexists(key, field);
            }
        }.getResult();
    }

    /* ======================================Strings====================================== */

    /**
     * 将字符串值 value 关联到 key 。 如果 key 已经持有其他值， setString 就覆写旧值，无视类型。 对于某个原本带有生存时间（TTL）的键来说， 当 setString 成功在这个键上执行时， 这个键原有的 TTL 将被清除。 时间复杂度：O(1)
     * 
     * @param key key
     * @param value string value
     * @return 在设置操作成功完成时，才返回 OK 。
     */
    public String setString(final String key, final String value) {
        return new Executor<String>(shardedJedisPool) {

            @Override
            String execute() {
                return jedis.set(key, value);
            }
        }.getResult();
    }

    /**
     * 将值 value 关联到 key ，并将 key 的生存时间设为 expire (以秒为单位)。 如果 key 已经存在， 将覆写旧值。 类似于以下两个命令: SET key value EXPIRE key expire # 设置生存时间
     * 不同之处是这个方法是一个原子性(atomic)操作，关联值和设置生存时间两个动作会在同一时间内完成，在 Redis 用作缓存时，非常实用。 时间复杂度：O(1)
     * 
     * @param key key
     * @param value string value
     * @param expire 生命周期
     * @return 设置成功时返回 OK 。当 expire 参数不合法时，返回一个错误。
     */
    public String setString(final String key, final String value, final int expire) {
        return new Executor<String>(shardedJedisPool) {

            @Override
            String execute() {
                return jedis.setex(key, expire, value);
            }
        }.getResult();
    }

    /**
     * 将 key 的值设为 value ，当且仅当 key 不存在。若给定的 key 已经存在，则 setStringIfNotExists 不做任何动作。 时间复杂度：O(1)
     * 
     * @param key key
     * @param value string value
     * @return 设置成功，返回 1 。设置失败，返回 0 。
     */
    public Long setStringIfNotExists(final String key, final String value) {
        return new Executor<Long>(shardedJedisPool) {

            @Override
            Long execute() {
                return jedis.setnx(key, value);
            }
        }.getResult();
    }

    /**
     * //TODO 将 key 的值设为 value ，当且仅当 key 不存在。若给定的 key 已经存在，则 setStringIfNotExists 不做任何动作。 时间复杂度：O(1)
     * 
     * @param key
     * @param value
     * @return
     */
    public String setObject(final String key, final Serializable value) {
        return new Executor<String>(shardedJedisPool) {

            @Override
            String execute() {
                return jedis.set(SafeEncoder.encode(key), SerializeKit.serialize(value));
            }
        }.getResult();
    }

    /**
     * //TODO 将 key 的值设为 value ，当且仅当 key 不存在。若给定的 key 已经存在，则 setStringIfNotExists 不做任何动作。 时间复杂度：O(1)
     * 
     * @param key
     * @param value
     * @param expire
     * @return
     */
    public String setObject(final String key, final Serializable value, final int expire) {
        return new Executor<String>(shardedJedisPool) {

            @Override
            String execute() {
                return jedis.setex(key.getBytes(), expire, SerializeKit.serialize(value));
            }
        }.getResult();
    }

    /**
     * //TODO 添加方法功能描述
     * 
     * @param key
     * @param field
     * @param value
     * @return
     */
    public Long hset(final String key, final String field, final Serializable value) {
        return new Executor<Long>(shardedJedisPool) {

            @Override
            Long execute() {
                return jedis.hset(key.getBytes(), field.getBytes(), SerializeKit.serialize(value));
            }
        }.getResult();
    }

    public Long hset(final String key, final String field, final Serializable value, final int expire) {
        return new Executor<Long>(shardedJedisPool) {

            @Override
            Long execute() {
                Pipeline pipeline = jedis.getShard(key).pipelined();
                Response<Long> result = pipeline.hset(key.getBytes(), field.getBytes(), SerializeKit.serialize(value));
                pipeline.expire(key, expire);
                pipeline.sync();
                return result.get();
            }
        }.getResult();
    }

    /**
     * 返回 key 所关联的字符串值。如果 key 不存在那么返回特殊值 nil 。 假如 key 储存的值不是字符串类型，返回一个错误，因为 getString 只能用于处理字符串值。 时间复杂度: O(1)
     * 
     * @param key key
     * @return 当 key 不存在时，返回 nil ，否则，返回 key 的值。如果 key 不是字符串类型，那么返回一个错误。
     */
    public String getString(final String key) {
        return new Executor<String>(shardedJedisPool) {

            @Override
            String execute() {
                return jedis.get(key);
            }
        }.getResult();
    }

    public <T extends Serializable> T get(final String key) {
        return new Executor<T>(shardedJedisPool) {

            @SuppressWarnings("unchecked")
            @Override
            T execute() {
                Object result = null;
                byte[] retVal = jedis.get(SafeEncoder.encode(key));
                if (null != retVal) {
                    try {
                        result = SerializeKit.unserialize(retVal);
                    } catch (Exception e) {
                        result = SafeEncoder.encode(retVal);
                    }
                }
                return (T) result;
            }
        }.getResult();
    }

    /**
     * 批量的 {@link #setString(String, String)}
     * 
     * @param pairs 键值对数组{数组第一个元素为key，第二个元素为value}
     * @return 操作状态的集合
     */
    public List<Object> batchSetString(final List<Pair<String, String>> pairs) {
        return new Executor<List<Object>>(shardedJedisPool) {

            @Override
            List<Object> execute() {
                ShardedJedisPipeline pipeline = jedis.pipelined();
                for (Pair<String, String> pair : pairs) {
                    pipeline.set(pair.getKey(), pair.getValue());
                }
                return pipeline.syncAndReturnAll();
            }
        }.getResult();
    }

    /**
     * 批量的 {@link #getString(String)}
     * 
     * @param keys key数组
     * @return value的集合
     */
    public List<String> batchGetString(final String[] keys) {
        return new Executor<List<String>>(shardedJedisPool) {

            @Override
            List<String> execute() {
                ShardedJedisPipeline pipeline = jedis.pipelined();
                List<String> result = new ArrayList<String>(keys.length);
                List<Response<String>> responses = new ArrayList<Response<String>>(keys.length);
                for (String key : keys) {
                    responses.add(pipeline.get(key));
                }
                pipeline.sync();
                for (Response<String> resp : responses) {
                    result.add(resp.get());
                }
                return result;
            }
        }.getResult();
    }

    /* ======================================Hashes====================================== */

    /**
     * 将哈希表 key 中的域 field 的值设为 value 。 如果 key 不存在，一个新的哈希表被创建并进行 hashSet 操作。 如果域 field 已经存在于哈希表中，旧值将被覆盖。 时间复杂度: O(1)
     * 
     * @param key key
     * @param field 域
     * @param value string value
     * @return 如果 field 是哈希表中的一个新建域，并且值设置成功，返回 1 。如果哈希表中域 field 已经存在且旧值已被新值覆盖，返回 0 。
     */
    public Long hashSet(final String key, final String field, final String value) {
        return new Executor<Long>(shardedJedisPool) {

            @Override
            Long execute() {
                return jedis.hset(key, field, value);
            }
        }.getResult();
    }

    /**
     * 将哈希表 key 中的域 field 的值设为 value 。 如果 key 不存在，一个新的哈希表被创建并进行 hashSet 操作。 如果域 field 已经存在于哈希表中，旧值将被覆盖。
     * 
     * @param key key
     * @param field 域
     * @param value string value
     * @param expire 生命周期，单位为秒
     * @return 如果 field 是哈希表中的一个新建域，并且值设置成功，返回 1 。如果哈希表中域 field 已经存在且旧值已被新值覆盖，返回 0 。
     */
    public Long hashSet(final String key, final String field, final String value, final int expire) {
        return new Executor<Long>(shardedJedisPool) {

            @Override
            Long execute() {
                Pipeline pipeline = jedis.getShard(key).pipelined();
                Response<Long> result = pipeline.hset(key, field, value);
                pipeline.expire(key, expire);
                pipeline.sync();
                return result.get();
            }
        }.getResult();
    }

    /**
     * 返回哈希表 key 中给定域 field 的值。 时间复杂度:O(1)
     * 
     * @param key key
     * @param field 域
     * @return 给定域的值。当给定域不存在或是给定 key 不存在时，返回 nil 。
     */
    public String hashGet(final String key, final String field) {
        return new Executor<String>(shardedJedisPool) {

            @Override
            String execute() {
                return jedis.hget(key, field);
            }
        }.getResult();
    }

    /**
     * 返回哈希表 key 中给定域 field 的值。 如果哈希表 key 存在，同时设置这个 key 的生存时间
     * 
     * @param key key
     * @param field 域
     * @param expire 生命周期，单位为秒
     * @return 给定域的值。当给定域不存在或是给定 key 不存在时，返回 nil 。
     */
    public String hashGet(final String key, final String field, final int expire) {
        return new Executor<String>(shardedJedisPool) {

            @Override
            String execute() {
                Pipeline pipeline = jedis.getShard(key).pipelined();
                Response<String> result = pipeline.hget(key, field);
                pipeline.expire(key, expire);
                pipeline.sync();
                return result.get();
            }
        }.getResult();
    }

    /**
     * 同时将多个 field-value (域-值)对设置到哈希表 key 中。 时间复杂度: O(N) (N为fields的数量)
     * 
     * @param key key
     * @param hash field-value的map
     * @return 如果命令执行成功，返回 OK 。当 key 不是哈希表(hash)类型时，返回一个错误。
     */
    public String hashMultipleSet(final String key, final Map<String, String> hash) {
        return new Executor<String>(shardedJedisPool) {

            @Override
            String execute() {
                return jedis.hmset(key, hash);
            }
        }.getResult();
    }

    /**
     * 同时将多个 field-value (域-值)对设置到哈希表 key 中。同时设置这个 key 的生存时间
     * 
     * @param key key
     * @param hash field-value的map
     * @param expire 生命周期，单位为秒
     * @return 如果命令执行成功，返回 OK 。当 key 不是哈希表(hash)类型时，返回一个错误。
     */
    public String hashMultipleSet(final String key, final Map<String, String> hash, final int expire) {
        return new Executor<String>(shardedJedisPool) {

            @Override
            String execute() {
                Pipeline pipeline = jedis.getShard(key).pipelined();
                Response<String> result = pipeline.hmset(key, hash);
                pipeline.expire(key, expire);
                pipeline.sync();
                return result.get();
            }
        }.getResult();
    }

    /**
     * 返回哈希表 key 中，一个或多个给定域的值。如果给定的域不存在于哈希表，那么返回一个 nil 值。 时间复杂度: O(N) (N为fields的数量)
     * 
     * @param key key
     * @param fields field的数组
     * @return 一个包含多个给定域的关联值的表，表值的排列顺序和给定域参数的请求顺序一样。
     */
    public List<String> hashMultipleGet(final String key, final String... fields) {
        return new Executor<List<String>>(shardedJedisPool) {

            @Override
            List<String> execute() {
                return jedis.hmget(key, fields);
            }
        }.getResult();
    }

    /**
     * 返回哈希表 key 中，一个或多个给定域的值。如果给定的域不存在于哈希表，那么返回一个 nil 值。 同时设置这个 key 的生存时间
     * 
     * @param key key
     * @param fields field的数组
     * @param expire 生命周期，单位为秒
     * @return 一个包含多个给定域的关联值的表，表值的排列顺序和给定域参数的请求顺序一样。
     */
    public List<String> hashMultipleGet(final String key, final int expire, final String... fields) {
        return new Executor<List<String>>(shardedJedisPool) {

            @Override
            List<String> execute() {
                Pipeline pipeline = jedis.getShard(key).pipelined();
                Response<List<String>> result = pipeline.hmget(key, fields);
                pipeline.expire(key, expire);
                pipeline.sync();
                return result.get();
            }
        }.getResult();
    }

    /**
     * 批量的{@link #hashMultipleSet(String, Map)}，在管道中执行
     * 
     * @param pairs 多个hash的多个field
     * @return 操作状态的集合
     */
    public List<Object> batchHashMultipleSet(final List<Pair<String, Map<String, String>>> pairs) {
        return new Executor<List<Object>>(shardedJedisPool) {

            @Override
            List<Object> execute() {
                ShardedJedisPipeline pipeline = jedis.pipelined();
                for (Pair<String, Map<String, String>> pair : pairs) {
                    pipeline.hmset(pair.getKey(), pair.getValue());
                }
                return pipeline.syncAndReturnAll();
            }
        }.getResult();
    }

    /**
     * 批量的{@link #hashMultipleSet(String, Map)}，在管道中执行
     * 
     * @param data Map<String, Map<String, String>>格式的数据
     * @return 操作状态的集合
     */
    public List<Object> batchHashMultipleSet(final Map<String, Map<String, String>> data) {
        return new Executor<List<Object>>(shardedJedisPool) {

            @Override
            List<Object> execute() {
                ShardedJedisPipeline pipeline = jedis.pipelined();
                for (Map.Entry<String, Map<String, String>> iter : data.entrySet()) {
                    pipeline.hmset(iter.getKey(), iter.getValue());
                }
                return pipeline.syncAndReturnAll();
            }
        }.getResult();
    }

    /**
     * 批量的{@link #hashMultipleGet(String, String...)}，在管道中执行
     * 
     * @param pairs 多个hash的多个field
     * @return 执行结果的集合
     */
    public List<List<String>> batchHashMultipleGet(final List<Pair<String, String[]>> pairs) {
        return new Executor<List<List<String>>>(shardedJedisPool) {

            @Override
            List<List<String>> execute() {
                ShardedJedisPipeline pipeline = jedis.pipelined();
                List<List<String>> result = new ArrayList<List<String>>(pairs.size());
                List<Response<List<String>>> responses = new ArrayList<Response<List<String>>>(pairs.size());
                for (Pair<String, String[]> pair : pairs) {
                    responses.add(pipeline.hmget(pair.getKey(), pair.getValue()));
                }
                pipeline.sync();
                for (Response<List<String>> resp : responses) {
                    result.add(resp.get());
                }
                return result;
            }
        }.getResult();

    }

    /**
     * 返回哈希表 key 中，所有的域和值。在返回值里，紧跟每个域名(field name)之后是域的值(value)，所以返回值的长度是哈希表大小的两倍。 时间复杂度: O(N)
     * 
     * @param key key
     * @return 以列表形式返回哈希表的域和域的值。若 key 不存在，返回空列表。
     */
    public Map<String, String> hashGetAll(final String key) {
        return new Executor<Map<String, String>>(shardedJedisPool) {

            @Override
            Map<String, String> execute() {
                return jedis.hgetAll(key);
            }
        }.getResult();
    }

    /**
     * 返回哈希表 key 中，所有的域和值。在返回值里，紧跟每个域名(field name)之后是域的值(value)，所以返回值的长度是哈希表大小的两倍。 同时设置这个 key 的生存时间
     * 
     * @param key key
     * @param expire 生命周期，单位为秒
     * @return 以列表形式返回哈希表的域和域的值。若 key 不存在，返回空列表。
     */
    public Map<String, String> hashGetAll(final String key, final int expire) {
        return new Executor<Map<String, String>>(shardedJedisPool) {

            @Override
            Map<String, String> execute() {
                Pipeline pipeline = jedis.getShard(key).pipelined();
                Response<Map<String, String>> result = pipeline.hgetAll(key);
                pipeline.expire(key, expire);
                pipeline.sync();
                return result.get();
            }
        }.getResult();
    }

    /**
     * 获取hash的field数量
     * 
     * @param key
     * @return
     */
    public Long hashLen(final String key) {
        return new Executor<Long>(shardedJedisPool) {

            @Override
            Long execute() {
                return jedis.hlen(key);
            }
        }.getResult();
    }

    /**
     * 获取hash的field数量
     * 
     * @param key
     * @return
     */
    public Long hashLen(final String key, final int expire) {
        return new Executor<Long>(shardedJedisPool) {

            @Override
            Long execute() {
                Pipeline pipeline = jedis.getShard(key).pipelined();
                Response<Long> result = pipeline.hlen(key);
                pipeline.expire(key, expire);
                pipeline.sync();
                return result.get();
            }
        }.getResult();
    }

    /**
     * 返回 hash 的所有 field
     * 
     * @param key
     * @return
     */
    public Set<String> hashKeys(final String key) {
        return new Executor<Set<String>>(shardedJedisPool) {

            @Override
            Set<String> execute() {
                return jedis.hkeys(key);
            }
        }.getResult();
    }

    /**
     * 返回 hash 的所有 field
     * 
     * @param key
     * @return
     */
    public Set<String> hashKeys(final String key, final int expire) {
        return new Executor<Set<String>>(shardedJedisPool) {

            @Override
            Set<String> execute() {
                Pipeline pipeline = jedis.getShard(key).pipelined();
                Response<Set<String>> result = pipeline.hkeys(key);
                pipeline.expire(key, expire);
                pipeline.sync();
                return result.get();
            }
        }.getResult();
    }

    /**
     * 批量的{@link #hashGetAll(String)}
     * 
     * @param keys key的数组
     * @return 执行结果的集合
     */
    public List<Map<String, String>> batchHashGetAll(final String... keys) {
        return new Executor<List<Map<String, String>>>(shardedJedisPool) {

            @Override
            List<Map<String, String>> execute() {
                ShardedJedisPipeline pipeline = jedis.pipelined();
                List<Map<String, String>> result = new ArrayList<Map<String, String>>(keys.length);
                List<Response<Map<String, String>>> responses = new ArrayList<Response<Map<String, String>>>(keys.length);
                for (String key : keys) {
                    responses.add(pipeline.hgetAll(key));
                }
                pipeline.sync();
                for (Response<Map<String, String>> resp : responses) {
                    result.add(resp.get());
                }
                return result;
            }
        }.getResult();
    }

    /**
     * 批量的{@link #hashMultipleGet(String, String...)}，与{@link #batchHashGetAll(String...)}不同的是，返回值为Map类型
     * 
     * @param keys key的数组
     * @return 多个hash的所有filed和value
     */
    public Map<String, Map<String, String>> batchHashGetAllForMap(final String... keys) {
        return new Executor<Map<String, Map<String, String>>>(shardedJedisPool) {

            @Override
            Map<String, Map<String, String>> execute() {
                ShardedJedisPipeline pipeline = jedis.pipelined();

                // 设置map容量防止rehash
                int capacity = 1;
                while ((int) (capacity * 0.75) <= keys.length) {
                    capacity <<= 1;
                }
                Map<String, Map<String, String>> result = new HashMap<String, Map<String, String>>(capacity);
                List<Response<Map<String, String>>> responses = new ArrayList<Response<Map<String, String>>>(keys.length);
                for (String key : keys) {
                    responses.add(pipeline.hgetAll(key));
                }
                pipeline.sync();
                for (int i = 0; i < keys.length; ++i) {
                    result.put(keys[i], responses.get(i).get());
                }
                return result;
            }
        }.getResult();
    }

    /* ======================================List====================================== */

    /**
     * 将一个或多个值 value 插入到列表 key 的表尾(最右边)。
     * 
     * @param key key
     * @param values value的数组
     * @return 执行 listPushTail 操作后，表的长度
     */
    public Long listPushTail(final String key, final String... values) {
        return new Executor<Long>(shardedJedisPool) {

            @Override
            Long execute() {
                return jedis.rpush(key, values);
            }
        }.getResult();
    }

    /**
     * 将一个或多个值 value 插入到列表 key 的表头
     * 
     * @param key key
     * @param value string value
     * @return 执行 listPushHead 命令后，列表的长度。
     */
    public Long listPushHead(final String key, final String... values) {
        return new Executor<Long>(shardedJedisPool) {

            @Override
            Long execute() {
                return jedis.lpush(key, values);
            }
        }.getResult();
    }

    /**
     * list长度
     * 
     * @param key
     * @return
     */
    public Long listLen(final String key) {
        return new Executor<Long>(shardedJedisPool) {

            @Override
            Long execute() {
                return jedis.llen(key);
            }
        }.getResult();
    }

    /**
     * 将一个或多个值 value 插入到列表 key 的表头, 当列表大于指定长度是就对列表进行修剪(trim)
     * 
     * @param key key
     * @param value string value
     * @param size 链表超过这个长度就修剪元素
     * @return 执行 listPushHeadAndTrim 命令后，列表的长度。
     */
    public Long listPushHeadAndTrim(final String key, final String value, final long size) {
        return new Executor<Long>(shardedJedisPool) {

            @Override
            Long execute() {
                Pipeline pipeline = jedis.getShard(key).pipelined();
                Response<Long> result = pipeline.lpush(key, value);
                // 修剪列表元素, 如果 size - 1 比 end 下标还要大，Redis将 size 的值设置为 end 。
                pipeline.ltrim(key, 0, size - 1);
                pipeline.sync();
                return result.get();
            }
        }.getResult();
    }

    /**
     * 移除从表尾到表头，第一个 value
     * 
     * @param key
     * @param value
     * @return
     */
    public Long listDel(final String key, final String value, final int index) {
        return new Executor<Long>(shardedJedisPool) {

            @Override
            Long execute() {
                return jedis.lrem(key, index, value);
            }
        }.getResult();
    }

    /**
     * 批量的{@link #listPushTail(String, String...)}，以锁的方式实现
     * 
     * @param key key
     * @param values value的数组
     * @param delOld 如果key存在，是否删除它。true 删除；false: 不删除，只是在行尾追加
     */
    public void batchListPushTail(final String key, final String[] values, final boolean delOld) {
        new Executor<Object>(shardedJedisPool) {

            @Override
            Object execute() {
                if (delOld) {
                    RedisLock lock = new RedisLock(key, shardedJedisPool);
                    lock.lock();
                    try {
                        Pipeline pipeline = jedis.getShard(key).pipelined();
                        pipeline.del(key);
                        for (String value : values) {
                            pipeline.rpush(key, value);
                        }
                        pipeline.sync();
                    } finally {
                        lock.unlock();
                    }
                } else {
                    jedis.rpush(key, values);
                }
                return null;
            }
        }.getResult();
    }

    /**
     * 同{@link #batchListPushTail(String, String[], boolean)},不同的是利用redis的事务特性来实现
     * 
     * @param key key
     * @param values value的数组
     * @return null
     */
    public Object updateListInTransaction(final String key, final List<String> values) {
        return new Executor<Object>(shardedJedisPool) {

            @Override
            Object execute() {
                Transaction transaction = jedis.getShard(key).multi();
                transaction.del(key);
                for (String value : values) {
                    transaction.rpush(key, value);
                }
                transaction.exec();
                return null;
            }
        }.getResult();
    }

    /**
     * 在key对应list的尾部部添加字符串元素,如果key存在，什么也不做
     * 
     * @param key key
     * @param values value的数组
     * @return 执行insertListIfNotExists后，表的长度
     */
    public Long insertListIfNotExists(final String key, final String[] values) {
        return new Executor<Long>(shardedJedisPool) {

            @Override
            Long execute() {
                RedisLock lock = new RedisLock(key, shardedJedisPool);
                lock.lock();
                try {
                    if (!jedis.exists(key)) {
                        return jedis.rpush(key, values);
                    }
                } finally {
                    lock.unlock();
                }
                return 0L;
            }
        }.getResult();
    }

    /**
     * 返回list所有元素，下标从0开始，负值表示从后面计算，-1表示倒数第一个元素，key不存在返回空列表
     * 
     * @param key key
     * @return list所有元素
     */
    public List<String> listGetAll(final String key) {
        return new Executor<List<String>>(shardedJedisPool) {

            @Override
            List<String> execute() {
                return jedis.lrange(key, 0, -1);
            }
        }.getResult();
    }

    /**
     * 返回指定区间内的元素，下标从0开始，负值表示从后面计算，-1表示倒数第一个元素，key不存在返回空列表
     * 
     * @param key key
     * @param beginIndex 下标开始索引（包含）
     * @param endIndex 下标结束索引（不包含）
     * @return 指定区间内的元素
     */
    public List<String> listRange(final String key, final long beginIndex, final long endIndex) {
        return new Executor<List<String>>(shardedJedisPool) {

            @Override
            List<String> execute() {
                return jedis.lrange(key, beginIndex, endIndex - 1);
            }
        }.getResult();
    }

    /**
     * 一次获得多个链表的数据
     * 
     * @param keys key的数组
     * @return 执行结果
     */
    public Map<String, List<String>> batchGetAllList(final List<String> keys) {
        return new Executor<Map<String, List<String>>>(shardedJedisPool) {

            @Override
            Map<String, List<String>> execute() {
                ShardedJedisPipeline pipeline = jedis.pipelined();
                Map<String, List<String>> result = new HashMap<String, List<String>>();
                List<Response<List<String>>> responses = new ArrayList<Response<List<String>>>(keys.size());
                for (String key : keys) {
                    responses.add(pipeline.lrange(key, 0, -1));
                }
                pipeline.sync();
                for (int i = 0; i < keys.size(); ++i) {
                    result.put(keys.get(i), responses.get(i).get());
                }
                return result;
            }
        }.getResult();
    }

    /* ======================================Pub/Sub====================================== */

    /**
     * 将信息 message 发送到指定的频道 channel。 时间复杂度：O(N+M)，其中 N 是频道 channel 的订阅者数量，而 M 则是使用模式订阅(subscribed patterns)的客户端的数量。
     * 
     * @param channel 频道
     * @param message 信息
     * @return 接收到信息 message 的订阅者数量。
     */
    public Long publish(final String channel, final String message) {
        return new Executor<Long>(shardedJedisPool) {

            @Override
            Long execute() {
                Jedis _jedis = jedis.getShard(channel);
                return _jedis.publish(channel, message);
            }

        }.getResult();
    }

    /**
     * 订阅给定的一个频道的信息。
     * 
     * @param jedisPubSub 监听器
     * @param channel 频道
     */
    public void subscribe(final JedisPubSub jedisPubSub, final String channel) {
        new Executor<Object>(shardedJedisPool) {

            @Override
            Object execute() {
                Jedis _jedis = jedis.getShard(channel);
                // 注意subscribe是一个阻塞操作，因为当前线程要轮询Redis的响应然后调用subscribe
                _jedis.subscribe(jedisPubSub, channel);
                return null;
            }
        }.getResult();
    }

    /**
     * 取消订阅
     * 
     * @param jedisPubSub 监听器
     */
    public void unSubscribe(final JedisPubSub jedisPubSub) {
        jedisPubSub.unsubscribe();
    }

    /* ======================================Sorted set================================= */

    /**
     * sadd添加一个元素
     * 
     * @param key
     * @param members
     * @return
     */
    public Long sadd(final String key, final String... members) {
        return new Executor<Long>(shardedJedisPool) {

            @Override
            Long execute() {
                return jedis.sadd(key, members);
            }
        }.getResult();
    }
    
    public Long sadd(final String key, final byte[]... members) {
        return new Executor<Long>(shardedJedisPool) {

            @Override
            Long execute() {
                return jedis.sadd(key.getBytes(), members);
            }
        }.getResult();
    }

    /**
     * 获取key的集合
     * 
     * @param key
     * @return
     */
    public Set<String> smembers(final String key) {
        return new Executor<Set<String>>(shardedJedisPool) {

            @Override
            Set<String> execute() {
                return jedis.smembers(key);
            }
        }.getResult();
    }

    /**
     * 移除set元素
     * 
     * @param key
     * @param members
     * @return
     */
    public Long srem(final String key, final String... members) {
        return new Executor<Long>(shardedJedisPool) {

            @Override
            Long execute() {
                return jedis.srem(key, members);
            }
        }.getResult();
    }

    /**
     * 统计key的value重复合集(共同关注)
     * 
     * @param keys
     * @return
     */
    public Set<String> sinter(final String... keys) {
        return new Executor<Set<String>>(shardedJedisPool) {

            @Override
            Set<String> execute() {
                Jedis _jedis = jedis.getShard(keys[0]);
                return _jedis.sinter(keys);
            }
        }.getResult();
    }

    /**
     * 并集
     * 
     * @param keys
     * @return
     */
    public Set<String> sunion(final String... keys) {
        return new Executor<Set<String>>(shardedJedisPool) {

            @Override
            Set<String> execute() {
                Jedis _jedis = jedis.getShard(keys[0]);
                return _jedis.sunion(keys);
            }
        }.getResult();
    }

    /**
     * 差集
     * 
     * @param keys
     * @return
     */
    public Set<String> sdiff(final String... keys) {
        return new Executor<Set<String>>(shardedJedisPool) {

            @Override
            Set<String> execute() {
                Jedis _jedis = jedis.getShard(keys[0]);
                return _jedis.sdiff(keys);
            }
        }.getResult();
    }

    /**
     * 判断元素是否存在
     * 
     * @param key
     * @param member
     * @return
     */
    public Boolean sismember(final String key, final String member) {
        return new Executor<Boolean>(shardedJedisPool) {

            @Override
            Boolean execute() {
                return jedis.sismember(key, member);
            }
        }.getResult();
    }

    /**
     * 统计列表集合
     * 
     * @param key
     * @return
     */
    public Long scard(final String key) {
        return new Executor<Long>(shardedJedisPool) {

            @Override
            Long execute() {
                return jedis.scard(key);
            }
        }.getResult();
    }

    /**
     * 将一个 member 元素及其 score 值加入到有序集 key 当中。
     * 
     * @param key key
     * @param score score 值可以是整数值或双精度浮点数。
     * @param member 有序集的成员
     * @return 被成功添加的新成员的数量，不包括那些被更新的、已经存在的成员。
     */
    public Long addWithSortedSet(final String key, final double score, final String member) {
        return new Executor<Long>(shardedJedisPool) {

            @Override
            Long execute() {
                return jedis.zadd(key, score, member);
            }
        }.getResult();
    }

    /**
     * 将多个 member 元素及其 score 值加入到有序集 key 当中。
     * 
     * @param key key
     * @param scoreMembers score、member的pair
     * @return 被成功添加的新成员的数量，不包括那些被更新的、已经存在的成员。
     */
    public Long addWithSortedSet(final String key, final Map<String, Double> scoreMembers) {
        return new Executor<Long>(shardedJedisPool) {

            @Override
            Long execute() {
                return jedis.zadd(key, scoreMembers);
            }
        }.getResult();
    }

    /**
     * 返回有序集 key 中，指定区间内的成员。 按照从小到大排序
     * 
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set<String> rangeSortSet(final String key, final int start, final int end) {
        return new Executor<Set<String>>(shardedJedisPool) {

            @Override
            Set<String> execute() {
                return jedis.zrange(key, start, end);
            }
        }.getResult();
    }
    
    /**
     * 返回有序集 key 中，指定区间内的成员。 按照从大到小排序
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set<String> revrangeSortSet(final String key, final int start, final int end) {
        return new Executor<Set<String>>(shardedJedisPool) {

            @Override
            Set<String> execute() {
                return jedis.zrevrange(key, start, end);
            }
        }.getResult();
    }
    
    /**
     * 返回有序集 key 中， score 值介于 max 和 min 之间(默认包括等于 max 或 min )的所有的成员。 有序集成员按 score 值递减(从大到小)的次序排列。
     * 
     * @param key key
     * @param max score最大值
     * @param min score最小值
     * @return 指定区间内，带有 score 值(可选)的有序集成员的列表
     */
    public Set<String> revrangeByScoreWithSortedSet(final String key, final double max, final double min) {
        return new Executor<Set<String>>(shardedJedisPool) {

            @Override
            Set<String> execute() {
                return jedis.zrevrangeByScore(key, max, min);
            }
        }.getResult();
    }

    /* ======================================Other====================================== */

    /**
     * 构造Pair键值对
     * 
     * @param key key
     * @param value value
     * @return 键值对
     */
    public <K, V> Pair<K, V> makePair(K key, V value) {
        return new Pair<K, V>(key, value);
    }

    /**
     * 键值对
     * 
     * @version V1.0
     * @author fengjc
     * @param <K> key
     * @param <V> value
     */
    public class Pair<K, V> {

        private K key;

        private V value;

        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public void setKey(K key) {
            this.key = key;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }
    }

	public <T extends Serializable> T hgetObj(final String key, final String field) {
        return new Executor<T>(shardedJedisPool) {

            @SuppressWarnings("unchecked")
            @Override
            T execute() {
                Object result = null;
                
                byte[] retVal = jedis.hget(SafeEncoder.encode(key), SafeEncoder.encode(field));
                if (null != retVal) {
                    try {
                        result = SerializeKit.unserialize(retVal);
                    } catch (Exception e) {
                        result = SafeEncoder.encode(retVal);
                    }	
                }
                return (T) result;
            }
        }.getResult();
    }

}
