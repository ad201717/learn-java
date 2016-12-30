package com.learn.howe.springBoot.todo.cache.base;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.serializer.support.DeserializingConverter;
import org.springframework.core.serializer.support.SerializingConverter;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

/**
 * @Author Karl
 * @Date 2016/12/30 17:59
 */
public class RedisObjectSerializer implements RedisSerializer<Object> {

    private Converter<Object, byte[]> serializer = new SerializingConverter();
    private Converter<byte[], Object> deserializer = new DeserializingConverter();

    static final byte[] EMPTY_ARRAR = new byte[0];

    @Override
    public byte[] serialize(Object o) throws SerializationException {
        if (null == o) {
            return EMPTY_ARRAR;
        }

        try {
            return serializer.convert(o);
        } catch (Exception e) {
            return EMPTY_ARRAR;
        }
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        if (null == bytes || bytes.length == 0) {
            return null;
        }

        try {
            return deserializer.convert(bytes);
        } catch (Exception e) {
            throw new SerializationException("can`t deserialize", e);
        }
    }
}
