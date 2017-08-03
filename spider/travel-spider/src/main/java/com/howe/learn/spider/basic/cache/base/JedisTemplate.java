package com.howe.learn.spider.basic.cache.base;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.exceptions.JedisException;

import java.util.List;

/**
 * @Author Karl
 * @Date 2016/1/4 17:06
 */
public class JedisTemplate {

    private JedisPool jedisPool;

    public JedisPool getJedisPool() {
        return jedisPool;
    }

    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public JedisTemplate(JedisPool jedisPool){
        this.jedisPool = jedisPool;
    }

    public static interface JedisAction<T>{
        T action(Jedis jedis);
    }
    public static interface JedisActionNoResult{
        void action(Jedis jedis);
    }
    public static interface JedisPipelineAction{
       List<Object> action(Pipeline pipeline);
    }
    public static interface JedisPipelineActionNoResult{
        void action(Pipeline pipeline);
    }

    public <T> T execute(JedisAction<T> jedisAction) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedisAction.action(jedis);
        } catch (JedisException e) {
            throw e;
        } finally {
            if(null != jedis){
                jedis.close();
            }
        }
    }

    public void execute(JedisActionNoResult jedisAction){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedisAction.action(jedis);
        } catch (JedisException e) {
            throw e;
        } finally {
            if(null != jedis){
                jedis.close();
            }
        }
    }

    public List<Object> execute(JedisPipelineAction jedisAction){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            Pipeline pipeline = jedis.pipelined();
            jedisAction.action(pipeline);
            return pipeline.syncAndReturnAll();
        } catch (JedisException e) {
            throw e;
        } finally {
            if(null != jedis){
                jedis.close();
            }
        }
    }

    public void execute(JedisPipelineActionNoResult jedisAction){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            Pipeline pipeline = jedis.pipelined();
            jedisAction.action(pipeline);
            pipeline.sync();
        } catch (JedisException e) {
            throw e;
        } finally {
            if(null != jedis){
                jedis.close();
            }
        }
    }
}
