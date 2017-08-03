package com.howe.learn.my.rocketmq.remoting.common;

import com.alibaba.fastjson.JSON;

import java.nio.charset.Charset;

/**
 * @Author Karl
 * @Date 2017/4/12 10:26
 */
public class RemotingSerialize {

    public static <T> T fromJson(final String json, Class<T> clazz) {
        return JSON.parseObject(json, clazz);
    }

    public static String toJson(final Object obj) {
        return JSON.toJSONString(obj, false);
    }

    public static <T> T decode(final byte[] bytes, Class<T> clazz) {
        return RemotingSerialize.fromJson(new String(bytes, Charset.forName("UTF-8")), clazz);
    }

    public static byte[] encode(final Object obj) {
        final String json = RemotingSerialize.toJson(obj);
        if (null != json) {
            return json.getBytes(Charset.forName("UTF-8"));
        }
        return null;
    }
}
