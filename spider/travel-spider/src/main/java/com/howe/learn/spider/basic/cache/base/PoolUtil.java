package com.howe.learn.spider.basic.cache.base;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.util.Properties;

/**
 * @Author Karl
 * @Date 2016/1/4 17:06
 */
public class PoolUtil {

    public static JedisPool getPool(){
        return InstanceHolder.pool;
    }

    private static class InstanceHolder {
        static final JedisPool pool = createPool();
        static final String CONFIG_FILE = "redis.properties";
        static final String DEFAULT_HOST = "";
        static final int DEFAULT_PORT = 6379;
        static String host = DEFAULT_HOST;
        static int port = DEFAULT_PORT;

        private static JedisPool createPool(){
            JedisPoolConfig poolConfig = new JedisPoolConfig();
            poolConfig.setMaxTotal(5);
            poolConfig.setMaxIdle(2);
            poolConfig.setMaxWaitMillis(60);

            initConfig();
            return new JedisPool(poolConfig, host, port);
        }

        private static void initConfig(){
            Properties prop = new Properties();
            try {
                prop.load(PoolUtil.class.getClassLoader().getResourceAsStream(CONFIG_FILE));
                host = prop.getProperty("redis.host");
                port = Integer.parseInt(prop.getProperty("redis.port"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
