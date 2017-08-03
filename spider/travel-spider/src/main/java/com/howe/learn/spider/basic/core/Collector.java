package com.howe.learn.spider.basic.core;

/**
 * @Author Karl
 * @Date 2017/1/4 17:11
 */
public interface Collector<T> {

    void collect(T t, long waitTimeMills) throws SpiderException;
}
