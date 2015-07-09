package blade.plugin.redis;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import blade.kit.SerializeKit;

/**
 * jedis tool
 * @author:biezhi
 * @version:1.0
 */
public class JedisCache extends RedisExecutor {

    public String set(final String key, final String value) {
        return super.setString(key, value);
    }

    public String set(final String key, final Serializable value) {
        return super.setObject(key, value);
    }

    public String set(final String key, final String value, final int timeout) {
        return super.setString(key, value, timeout);
    }

    public Long hset(final String key, final String field, final String value) {
        return super.hashSet(key, field, value);
    }

    public Long hset(final String key, final String field, final String value, final int timeout) {
        return super.hashSet(key, field, value, timeout);
    }

    public String hset(final String key, final Map<String, String> map) {
        return super.hashMultipleSet(key, map);
    }

    public String hset(final String key, Map<String, String> map, final int timeout) {
        return super.hashMultipleSet(key, map, timeout);
    }

    public Long hset(final String key, final String field, final Serializable value) {
        return super.hset(key, field, value);
    }

    public Long hset(final String key, final String field, final Serializable value, int timeout) {
        return super.hset(key, field, value, timeout);
    }

    public boolean exists(final String key, final String field) {
        return super.exists(key, field);
    }

    public boolean exists(final String key) {
        return super.exists(key);
    }

    public Long del(final String key) {
        return super.delKey(key);
    }

    public Long del(final String key, final String field) {
        return super.delKey(key, field);
    }

    public <T extends Serializable> T get(final String key) {
        return super.get(key);
    }

    public String get(final String key, final String field) {
        return super.hashGet(key, field);
    }

    public Map<String, String> getAllHash(final String key) {
        return super.hashGetAll(key);
    }

    public Serializable getModel(final String key, final String field) {
        return SerializeKit.unserialize(super.hashGet(key, field).getBytes());
    }

    public List<String> getSet(final String key, final Integer start, final Integer end) {
        return super.listRange(key, start, end);
    }

    public Set<String> getKeys(final String pattern) {
        return super.getKeyLike(pattern);
    }

    public Long delLike(final String patten) {
        return super.delKeysLike(patten);
    }

    public String hget(final String key, final String field) {
        return super.hashGet(key, field);
    }

    public String hget(final String key, final String field, final int timeout) {
        return super.hashGet(key, field, timeout);
    }

    public String hget(String key, Map<String, String> map) {
        return super.hashMultipleSet(key, map);
    }

    public Long hcount(String key) {
        return super.hashLen(key);
    }

    public Long sadd(String key, String... members) {
        return super.sadd(key, members);
    }

    public Long srem(String key, String... members) {
        return super.srem(key, members);
    }

    public Set<String> sunion(String... keys) {
        return super.sunion(keys);
    }

    public Set<String> sdiff(String... keys) {
        return super.sdiff(keys);
    }

    public Long scard(String key) {
        return super.scard(key);
    }

    public Long lpush(String key, String... values) {
        return super.listPushHead(key, values);
    }

    public Long rpush(String key, String... values) {
        return super.listPushTail(key, values);
    }

    public Long lpushTrim(String key, String value, long size) {
        return super.listPushHeadAndTrim(key, value, size);
    }

    public Long ldel(String key, String value) {
        return super.listDel(key, value, -1);
    }

    public Long llength(String key) {
        return super.listLen(key);
    }

	public <T extends Serializable> T hgetModel(String key, String field) {
		return super.hgetObj(key, field);
	}

}
