package com.howe.learn.spider.basic.core.support;

import com.howe.learn.spider.basic.core.Collector;
import com.howe.learn.spider.basic.core.SpiderException;
import com.howe.learn.spider.basic.core.SpiderMonitor;
import com.howe.learn.spider.basic.core.SpiderQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @Author Karl
 * @Date 2017/1/4 17:29
 */
public class DefaultSpiderQueue<T extends Wrapper<N>, N> implements SpiderQueue<T>, Collector<T> {

    private ArrayBlockingQueue<T> queue = new ArrayBlockingQueue<T>(10240, false);
    private ConcurrentHashMap<N, Object> searched = new ConcurrentHashMap<N, Object>();
    private Object obj = new Object();
    private String name;
    private SpiderMonitor spiderMonitor;

    public DefaultSpiderQueue(String name, SpiderMonitor spiderMonitor){
        this.name = name;
        this.spiderMonitor = spiderMonitor;
    }

    public void offer(T wrapper, long waitTimeMills) throws SpiderException{
        if(null != searched.putIfAbsent(wrapper.getObj(), obj)){
            return;
        }
        try {
            spiderMonitor.increment(wrapper.getPlatform().name().toLowerCase() + "-" + name + "-offer");
            queue.offer(wrapper, waitTimeMills, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            spiderMonitor.increment(name + "-offer-fail");
            throw new SpiderException(e.getMessage(), e);
        }
    }

    public T poll(long waitTimeMills) throws SpiderException {
        try {
            T wrapper = queue.poll(waitTimeMills, TimeUnit.MILLISECONDS);
            if(null != wrapper) {
                spiderMonitor.increment(wrapper.getPlatform().name().toLowerCase() + "-" + name + "-poll");
            }
            return wrapper;
        } catch (InterruptedException e) {
            spiderMonitor.increment(name + "-offer-fail");
            throw new SpiderException(e.getMessage(), e);
        }
    }

    @Override
    public void clear() throws SpiderException {
        queue.clear();
        searched.clear();
    }

    public void collect(T wrapper, long waitTimeMills) throws SpiderException {
        offer(wrapper, waitTimeMills);
    }

    public Logger getLog(){
        return LoggerFactory.getLogger(this.name);
    }
}
