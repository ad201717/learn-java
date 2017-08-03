package com.howe.learn.spider.basic.cache;

import com.howe.learn.spider.basic.cache.base.JedisTemplate;
import com.howe.learn.spider.basic.cache.base.PoolUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 * @Author Karl
 * @Date 2017/1/4 15:37
 */
public class BlacklistCache {

    private static final Logger log = LoggerFactory.getLogger(BlacklistCache.class);

    private static JedisTemplate jedisTemplate = new JedisTemplate(PoolUtil.getPool());

    private static final String BLACK_URL_SUFFIX = "Spider-blackUrl-";

    private BlacklistCache(){}

    private static BlacklistCache INSTANCE ;

    static {
        try {
            jedisTemplate.execute(new JedisTemplate.JedisActionNoResult() {
                public void action(Jedis jedis) {
                    jedis.get("1");
                }
            });
            INSTANCE = new BlacklistCache();
        } catch (JedisConnectionException e) {
            log.error("can`t connect to redis, Noop-cache enabled.!!!!!");
            INSTANCE = new Noop();
        }
    }

    public static BlacklistCache getInstance(){
        return INSTANCE;
    }

    public boolean exists(final String url) {
        try {
            return jedisTemplate.execute(new JedisTemplate.JedisAction<Boolean>() {
                public Boolean action(Jedis jedis) {
                    return null != jedis.get(BLACK_URL_SUFFIX + url);
                }
            });
        } catch (JedisConnectionException e) {
            log.error("can`t connect to redis, Noop-cache enabled.!!!!!");
            INSTANCE = new Noop();
            return false;
        }
    }

    public void add(final String url){
        try {
            jedisTemplate.execute(new JedisTemplate.JedisActionNoResult() {
                public void action(Jedis jedis) {
                    jedis.setex(BLACK_URL_SUFFIX + url, 86400, "0");
                }
            });
        } catch (JedisConnectionException e) {
            log.error("can`t connect to redis, Noop-cache enabled.!!!!!");
            INSTANCE = new Noop();
        }
    }

    static class Noop extends BlacklistCache{
        @Override
        public boolean exists(String url) {
            return false;
        }

        @Override
        public void add(String url) {

        }
    }

    public static void main(String[] args){
        String url = "http://www.baidu.com";
        boolean exists = BlacklistCache.getInstance().exists(url);
        System.out.println(exists);
        BlacklistCache.getInstance().add(url);
        exists = BlacklistCache.getInstance().exists(url);
        System.out.println(exists);
    }
}
