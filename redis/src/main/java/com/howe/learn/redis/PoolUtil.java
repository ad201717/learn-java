package com.howe.learn.redis;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @Author Karl
 * @Date 2016/12/9 17:06
 */
public class PoolUtil {

    public static JedisPool getPool(){
        return InstanceHolder.pool;
    }

    private static class InstanceHolder {
        static final JedisPool pool = createPool();

        private static JedisPool createPool(){
            JedisPoolConfig poolConfig = new JedisPoolConfig();
            poolConfig.setMaxTotal(20);
            poolConfig.setMaxIdle(5);
            poolConfig.setMaxWaitMillis(60);

            return new JedisPool(poolConfig, "127.0.0.1", 6379);
        }
    }
}
