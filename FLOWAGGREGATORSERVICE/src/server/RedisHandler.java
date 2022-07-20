package server;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

import model.Constants;
import model.FlowListCacheInterface;
import model.FlowLongStatsRecord;
import model.StatsCacheInterface;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Response;

public class RedisHandler implements StatsCacheInterface {
	public Jedis jedis;
	JedisPool pool;
	Boolean uses_jedis_pool;
	public RedisHandler(Boolean uses_jedis_pool) {
		this.uses_jedis_pool = uses_jedis_pool;
		if (this.uses_jedis_pool) {
			JedisPoolConfig config = new JedisPoolConfig();
			config.setMaxActive(64);
			config.setMaxIdle(10);
			config.setMaxWait(10);
			pool = new JedisPool(config, Constants.REDIS_SERVER, Constants.REDIS_PORT);
		}
	}
	
	public void InitConnection() {
		try {
			if (this.uses_jedis_pool) {
				jedis= pool.getResource();				
			} else {
				jedis = new Jedis(Constants.REDIS_SERVER, Constants.REDIS_PORT);
			}
			jedis.connect();

		} catch (Exception e) {
			System.err.println("Server could not connect to Redis server.");
		}
	}
	@Override
	public Boolean put(String key, FlowLongStatsRecord value) {
		jedis.set(key, value.getTxCount()+";"+value.getRxCount());
		return true;
	}
	@Override
	public FlowLongStatsRecord get(String key) {
		String stats = jedis.get(key);
		if (stats == null) {
			return new FlowLongStatsRecord();
		}
		String[] stats_arr = stats.split(";");
		if (stats_arr.length != 2) {
			return new FlowLongStatsRecord();
		}
		return new FlowLongStatsRecord(Long.parseLong(stats_arr[0]),Long.parseLong(stats_arr[1]));
	}
	@Override
	public Boolean put(String key, String value) {
		jedis.sadd(key, value);
		return true;
	}
	@Override
	public Set<String> getMembers(String key) {
		Set<String> cached_values = jedis.smembers(key);
		if (cached_values == null) {
			return new HashSet<String>();
		}
		return cached_values;
	}
}