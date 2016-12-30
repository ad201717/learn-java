package com.learn.howe.springBoot.todo.cache;

import com.learn.howe.springBoot.todo.cache.base.RedisObjectSerializer;
import com.learn.howe.springBoot.todo.domain.Rank;
import com.learn.howe.springBoot.todo.domain.Todo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @Author Karl
 * @Date 2016/12/30 18:16
 */
@Configuration
public class RedisConfig {

    public static final String RANK_KEY = "rank-list";

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory();
    }

    @Bean
    public RedisTemplate<String, Rank> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Rank> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new RedisObjectSerializer());
        return redisTemplate;
    }
}
