package com.howe.learn.spider.basic.core.worker;

import com.howe.learn.spider.basic.core.Spider;
import com.howe.learn.spider.basic.core.SpiderException;
import com.howe.learn.spider.basic.core.SpiderJob;
import com.howe.learn.spider.basic.core.SpiderMonitor;
import com.howe.learn.spider.basic.core.processor.SpiderProcessor;
import com.howe.learn.spider.basic.core.support.Wrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author Karl
 * @Date 2017/1/4 17:58
 */
public class CrawlWorker extends Thread {

    private static final Logger log = LoggerFactory.getLogger(CrawlWorker.class);

    public static final String NORMAL_NAME = "Spider-CrawlWorker";

    private static AtomicInteger counter = new AtomicInteger(0);

    private static long maxIdleTime = 60 * 1000;

    private volatile long lastWorkTime = System.currentTimeMillis();

    private Spider spider;

    private CountDownLatch exitLatch;

    private SpiderMonitor spiderMonitor;

    public CrawlWorker(Spider spider, CountDownLatch exitLatch, SpiderMonitor spiderMonitor){
        this.spider = spider;
        this.exitLatch = exitLatch;
        this.spiderMonitor = spiderMonitor;
        this.setName(NORMAL_NAME + "-" + counter.getAndIncrement());
    }

    @Override
    public void run() {
        log.info(Thread.currentThread().getName() + " started..");
        while(true){
            try {
                Wrapper<SpiderJob> jobWrapper = spider.getSpiderJobQueue().poll(3000);
                if(null != jobWrapper) {
                    if(jobWrapper.getObj() == SpiderJob.POISON){
                        log.info(Thread.currentThread().getName() + " exit..");
                        break;
                    }
                    SpiderProcessor processor = spider.getProcessorMap().get(jobWrapper.getObj().getProcessorType());
                    if(null == processor) {
                        throw new SpiderException("processorType " + jobWrapper.getObj().getProcessorType().name() + " has no processor registered!!");
                    }
                    processor.process(jobWrapper, spider.getStoreQueue(), spider.getSpiderJobQueue());
                    lastWorkTime = System.currentTimeMillis();
                } else if(System.currentTimeMillis() - lastWorkTime > maxIdleTime) {
                    log.error(Thread.currentThread().getName() + "archive idleTime(" + maxIdleTime + "ms), worker exit..");
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
}
