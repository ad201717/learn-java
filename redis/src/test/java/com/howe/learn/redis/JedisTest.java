package com.howe.learn.redis;
import org.junit.Assert;
import org.junit.Test;
import redis.clients.jedis.Jedis;

/**
 * @Author Karl
 * @Date 2016/12/9 16:25
 */
public class JedisTest {

    @Test
    public void test(){


        JedisTemplate jedisTemplate = new JedisTemplate(PoolUtil.getPool());

        jedisTemplate.execute((final Jedis jedis)->{jedis.set("test", "ad201717");});

        String value = jedisTemplate.execute((final Jedis jedis)->jedis.get("test"));

        Assert.assertNotSame("ad201718", value);
        Assert.assertEquals("ad201717", value);
        PoolUtil.getPool().close();
    }
}
