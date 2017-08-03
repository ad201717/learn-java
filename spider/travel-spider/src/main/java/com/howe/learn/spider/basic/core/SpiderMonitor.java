package com.howe.learn.spider.basic.core;

/**
 * @Author Karl
 * @Date 2017/1/5 19:13
 */
public interface SpiderMonitor {

    void start();

    void stop();

    void increment(String key);

    void decrement(String key);

    void incrementBy(String key, int count);
}
