package com.howe.learn.redis;

import redis.clients.jedis.Jedis;

import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 请求限速
 *
 * @Author Karl
 * @Date 2016/12/9 17:38
 */
public class RequestLimiter {
    private JedisTemplate jedisTemplate = new JedisTemplate(PoolUtil.getPool());

    private RequestLimiter(){}

    private static final RequestLimiter INSTANCE = new RequestLimiter();
    public static RequestLimiter getInstance(){
        return INSTANCE;
    }

    public boolean canPass(String method, String ip, final int maxTps){
        final String key = "requestLimit:" + ip + ":" + method;
        final long now = System.currentTimeMillis() / 1000;
        long count = jedisTemplate.execute((final Jedis jedis)->jedis.zcount(key, now, now));
        if(count >= maxTps) {
            return false;
        }
//        System.out.println(now + " count " + count);
        jedisTemplate.execute((final Jedis jedis)->jedis.zadd(key, now, String.valueOf(count)));
        if(count == 0){
            jedisTemplate.execute((final Jedis jedis)->jedis.zremrangeByScore(key, 0, now - 1));
        }
        return true;
    }

    public void close(){
        jedisTemplate.getJedisPool().close();
    }
}
