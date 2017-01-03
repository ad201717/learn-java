package com.howe.learn.serialization.protostuff;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

/**
 * @Author Karl
 * @Date 2017/1/3 14:12
 */
public class ProtostuffSerializeUtil {

    public static <T> byte[] serialize(T t) {
        Class<T> clazz = (Class<T>) t.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            Schema<T> schema = RuntimeSchema.getSchema(clazz);
            return ProtostuffIOUtil.toByteArray(t, schema, buffer);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        } finally {
            buffer.clear();
        }
    }

    public static <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try {
            T t = clazz.newInstance();
            Schema<T> schema = RuntimeSchema.getSchema(clazz);
            ProtostuffIOUtil.mergeFrom(bytes, t, schema);
            return t;
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}
