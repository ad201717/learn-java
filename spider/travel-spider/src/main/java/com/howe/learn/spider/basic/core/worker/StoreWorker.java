package com.howe.learn.spider.basic.core.worker;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import com.howe.learn.spider.basic.core.Spider;
import com.howe.learn.spider.basic.core.SpiderException;
import com.howe.learn.spider.basic.core.SpiderMonitor;
import com.howe.learn.spider.basic.core.support.Wrapper;
import com.howe.learn.spider.basic.domain.Route;
import com.howe.learn.spider.basic.domain.ScenicSpot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author Karl
 * @Date 2017/1/5 16:34
 */
public class StoreWorker extends Thread{

    private static final Logger log = LoggerFactory.getLogger(StoreWorker.class);

    public static final String NORMAL_NAME = "Spider-StoreWorker";

    private static AtomicInteger counter = new AtomicInteger(0);

    private static long maxIdleTime = 2 * 60 * 1000;

    private volatile long lastWorkTime = System.currentTimeMillis();

    private Spider spider;

    private CountDownLatch exitLatch;

    private SpiderMonitor spiderMonitor;

    public StoreWorker(Spider spider, CountDownLatch exitLatch, SpiderMonitor spiderMonitor){
        this.spider = spider;
        this.exitLatch = exitLatch;
        this.spiderMonitor = spiderMonitor;
        this.setName(NORMAL_NAME + "-" + counter.getAndIncrement());
    }

    @Override
    public void run() {
        log.info(Thread.currentThread().getName() + " started..");
        while(true){
            Wrapper wrapper = null;
            try {
                wrapper = spider.getStoreQueue().poll(3000);
                if(null != wrapper) {
                    if(wrapper.getObj() instanceof Route) {
                        this.storeRoute(wrapper);
                    } else if (wrapper.getObj() instanceof ScenicSpot) {
                        this.storeScenic(wrapper);
                    } else {
                        log.error("unknown store item :" + wrapper);
                    }
                    lastWorkTime = System.currentTimeMillis();
                } else if(!spider.isCrawlWorkerRunning() && System.currentTimeMillis() - lastWorkTime > maxIdleTime) {
                    log.error(Thread.currentThread().getName() + " archive idleTime(" + maxIdleTime + "ms), worker exit..");
                    break;
                }

                Thread.sleep(500);
            } catch (SpiderException e) {
                log.error(Thread.currentThread().getName() + " " + e.getMessage(), e);
                break;
            } catch (Exception e) {
               log.error(Thread.currentThread().getName() + " " + e.getMessage(), e);
            }
        }
        spiderMonitor.decrement(NORMAL_NAME);
        exitLatch.countDown();
    }

    private void storeRoute(Wrapper<Route> wrapper){
        Route route = wrapper.getObj();
        log.info(Thread.currentThread().getName() + " store route : " + route.getRouteName() + " from platform:" + wrapper.getPlatform());

        //store route

        spiderMonitor.increment("Store-Route");
    }

    private void storeScenic(Wrapper<ScenicSpot> wrapper) {
        //store scenic
    }
}
