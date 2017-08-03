package com.howe.learn.spider.basic.core.monitor;

import com.howe.learn.spider.basic.core.Spider;
import com.howe.learn.spider.basic.core.SpiderMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author Karl
 * @Date 2017/1/5 19:18
 */
public class DefaultSpiderMonitor implements SpiderMonitor {

    private static final Logger log = LoggerFactory.getLogger(DefaultSpiderMonitor.class);

    private ConcurrentHashMap<String, AtomicInteger> counters = new ConcurrentHashMap<>();

    private static final int MAX_IDLE_COUNT = 3;

    private volatile int idleCount = 0;

    private volatile long lastTwoUpdate = System.currentTimeMillis();

    private volatile long lastUpdate = System.currentTimeMillis();

    private Spider spider;

    private Thread monitorThread;

    public DefaultSpiderMonitor(Spider spider){
        this.spider = spider;
    }

    @Override
    public void start() {
        monitorThread = new Thread(){
            @Override
            public void run() {
                log.info("default monitor started.");
                while(DefaultSpiderMonitor.this.spider.isRunning()){
                    if (lastUpdate == lastTwoUpdate) {
                        idleCount++;
                    }
                    if (idleCount <= MAX_IDLE_COUNT) {
                        lastTwoUpdate = lastUpdate;
                        DefaultSpiderMonitor.this.printStatus();
                    }

                    try {
                        Thread.sleep(5*1000);
                    } catch (InterruptedException e) {
                        log.info("default monitor exit.");
                        return;
                    }
                }
                log.info("default monitor exit.");
            }
        };
        monitorThread.setName("Spider-monitor-default");
        monitorThread.setDaemon(true);
        monitorThread.start();
    }

    @Override
    public void stop() {
        monitorThread.interrupt();
        monitorThread = null;
    }

    private void beforeUpdate(){
        lastTwoUpdate = lastUpdate;
        lastUpdate = System.currentTimeMillis();

        idleCount = 0;
    }

    private AtomicInteger get(String key){
        AtomicInteger counter = counters.get(key);
        if(null == counter){
            counter = new AtomicInteger(0);
            AtomicInteger oldCounter = counters.putIfAbsent(key, counter);
            if(null != oldCounter){
                counter = oldCounter;
            }
        }
        return counter;
    }

    @Override
    public void increment(String key) {
        beforeUpdate();

        get(key).incrementAndGet();
    }

    @Override
    public void decrement(String key) {
        beforeUpdate();

        get(key).decrementAndGet();
    }

    @Override
    public void incrementBy(String key, int count) {
        beforeUpdate();

        get(key).addAndGet(count);
    }

    private void printStatus(){
        String ls = System.getProperty("line.separator");
        StringBuilder msg = new StringBuilder("status:").append(ls);
        Optional<String> maxLengthKey = counters.keySet().stream().max((s1, s2)->s1.length()-s2.length());
        int maxlen = maxLengthKey.isPresent() ? maxLengthKey.get().length() : 20;
        for(Map.Entry<String, AtomicInteger> entry : counters.entrySet()){
            for(int i = 0; i < maxlen - entry.getKey().length(); i++){
                msg.append(" ");
            }
            msg.append(entry.getKey())
                    .append(" : ")
                    .append(String.format("%1$9d", entry.getValue().get()))
                    .append(ls);
        }
        log.info(msg.toString());
    }
}

