package com.howe.learn.spider.basic.core;

/**
 * 爬虫队列
 *
 * @Author Karl
 * @Date 2017/1/4 16:51
 */
public interface SpiderQueue<T> extends Collector<T>{

    void offer(T t, long waitTimeMills) throws SpiderException;

    T poll(long waitTimeMills) throws SpiderException;

    void clear() ;
}
