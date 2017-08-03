package com.howe.learn.spider.basic.core.support;

import com.howe.learn.spider.basic.core.platform.PlatformEnum;
import org.springframework.util.Assert;

/**
 * Created by hao on 2017/1/7.
 */
public final class Wrapper<T> {

    private T obj;

    private PlatformEnum platform;

    private Wrapper(){}

    public static <T> Wrapper wrap(T obj, PlatformEnum platform) {
        Assert.notNull(obj);
        Assert.notNull(platform);

        Wrapper wrap = new Wrapper<>();
        wrap.obj = obj;
        wrap.platform = platform;
        return wrap;
    }

    public T getObj() {
        return obj;
    }

    public PlatformEnum getPlatform() {
        return platform;
    }

    @Override
    public String toString() {
        return "Wrapper{" +
                "obj=" + obj +
                ", platform=" + platform +
                '}';
    }
}
