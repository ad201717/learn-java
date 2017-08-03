package com.howe.learn.spider.basic.core;

import com.howe.learn.spider.basic.core.extractor.ExtractorTypeEnum;
import com.howe.learn.spider.basic.core.extractor.SpiderExtractor;
import com.howe.learn.spider.basic.core.monitor.DefaultSpiderMonitor;
import com.howe.learn.spider.basic.core.platform.PlatformEnum;
import com.howe.learn.spider.basic.core.processor.ExtractorProcessor;
import com.howe.learn.spider.basic.core.processor.ProcessorTypeEnum;
import com.howe.learn.spider.basic.core.processor.SpiderProcessor;
import com.howe.learn.spider.basic.core.support.DefaultSpiderQueue;
import com.howe.learn.spider.basic.core.support.Wrapper;
import com.howe.learn.spider.basic.core.worker.CrawlWorker;
import com.howe.learn.spider.basic.core.worker.StoreWorker;
import com.howe.learn.spider.basic.domain.Route;
import com.howe.learn.spider.util.PropertiesUtil;
import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 爬虫核心
 *
 * @Author Karl
 * @Date 2017/1/4 17:18
 */
public class Spider {

    private static final Logger log = LoggerFactory.getLogger(Spider.class);

    private SpiderMonitor spiderMonitor = new DefaultSpiderMonitor(this);

    private SpiderQueue<Wrapper<SpiderJob>> spiderJobQueue = new DefaultSpiderQueue<>("spiderJobQueue", spiderMonitor);

    private SpiderQueue<Wrapper<Object>> storeQueue = new DefaultSpiderQueue<>("storeQueue", spiderMonitor);

    private Map<ProcessorTypeEnum, SpiderProcessor> processorMap = new HashMap<ProcessorTypeEnum, SpiderProcessor>();

    private Map<ExtractorTypeEnum, SpiderExtractor> extractorMap = new HashMap<ExtractorTypeEnum, SpiderExtractor>();

    private CrawlWorker[] crawlWorkers;

    private StoreWorker[] storeWorkers;

    private static final int DEFAULT_CRAWL_WORKERS = 8;

    private static final int DEFAULT_STORE_WORKERS = 4;

    private static int crawlWorkerCount = DEFAULT_CRAWL_WORKERS;

    private static int storeWorkerCount = DEFAULT_STORE_WORKERS;

    private static final String CRAWL_WORKER_COUNT_KEY = "spider.crawl.worker.count";

    private static final String STORE_WORKER_COUNT_KEY = "spider.store.worker.count";

    private AtomicBoolean isRunning = new AtomicBoolean(false);

    private CountDownLatch workingcCrawlWorkerCount ;

    private CountDownLatch workingcStoreWorkerCount ;

    private Spider(){}

    private static Spider spider = new Spider();

    public static Spider getInstance(){
        return spider;
    }

    public void start(){
        if(isRunning.compareAndSet(false, true)){
            try {
                checkProcessors();
                checkExtractors();
            } catch (SpiderException e) {
                log.error("!!!!!!!!!!  Spider start fail, " + e.getMessage() + "   !!!!!!!!!!!");
                isRunning.set(false);
                return;
            }

            if(StringUtil.isNumeric(PropertiesUtil.get(CRAWL_WORKER_COUNT_KEY))){
                crawlWorkerCount = Integer.parseInt(PropertiesUtil.get(CRAWL_WORKER_COUNT_KEY));
            }
            if(StringUtil.isNumeric(PropertiesUtil.get(STORE_WORKER_COUNT_KEY))){
                storeWorkerCount = Integer.parseInt(PropertiesUtil.get(STORE_WORKER_COUNT_KEY));
            }

            log.info(String.format("Spider init with %s crawlWorker, %s storeWorker.", crawlWorkerCount, storeWorkerCount));

            workingcCrawlWorkerCount = new CountDownLatch(crawlWorkerCount);
            workingcStoreWorkerCount = new CountDownLatch(storeWorkerCount);

            crawlWorkers = new CrawlWorker[crawlWorkerCount];
            for(int i = 0; i < crawlWorkerCount; i++) {
                crawlWorkers[i] = new CrawlWorker(this, workingcCrawlWorkerCount, spiderMonitor);
                crawlWorkers[i].start();
            }

            storeWorkers = new StoreWorker[storeWorkerCount];
            for(int i = 0; i < storeWorkerCount; i++) {
                storeWorkers[i] = new StoreWorker(this, workingcStoreWorkerCount, spiderMonitor);
                storeWorkers[i].start();
            }
            spiderMonitor.incrementBy(CrawlWorker.NORMAL_NAME, crawlWorkerCount);
            spiderMonitor.incrementBy(StoreWorker.NORMAL_NAME, storeWorkerCount);

            spiderMonitor.start();

            log.info("spider started now...");
        }
    }

    public void awaitTerminal(){
        try {
            if(null != workingcCrawlWorkerCount) {
                workingcCrawlWorkerCount.await();
            }
            if(null != workingcStoreWorkerCount) {
                workingcStoreWorkerCount.await();
            }
        } catch (InterruptedException e) {
            shutdownNow();
        } finally {
            isRunning.set(false);

            spiderJobQueue.clear();
            storeQueue.clear();

            spiderMonitor.stop();
            log.info("Spider stopped....");
        }
    }

    public void shutdown(long waitTimeMills){
        if(isRunning.compareAndSet(true, false)){
            long till = System.currentTimeMillis() + waitTimeMills;
            long wait = waitTimeMills;
            try {
                for (int i = 0; i < crawlWorkerCount; i++) {
                    if(wait <= 0){
                        throw new SpiderException("shutdown timeout");
                    }
                    getSpiderJobQueue().offer(Wrapper.wrap(SpiderJob.POISON, PlatformEnum.DEFAULT), wait);
                    wait = till - System.currentTimeMillis();
                }
            } catch (SpiderException e) {
                this.shutdownNow();
            }

            spiderMonitor.stop();
        }
    }

    public void shutdownNow(){
        isRunning.set(false);

        for(CrawlWorker crawlWorker : crawlWorkers){
            crawlWorker.interrupt();
            workingcCrawlWorkerCount.countDown();
        }
        for(StoreWorker storeWorker : storeWorkers){
            storeWorker.interrupt();
            workingcStoreWorkerCount.countDown();
        }

    }

    public boolean isRunning(){
        return isRunning.get();
    }

    public boolean isCrawlWorkerRunning(){
        return workingcCrawlWorkerCount.getCount() > 0;
    }

    public void newJob(Wrapper<SpiderJob> job) {
        this.spiderJobQueue.offer(job, 3000);
    }

    public void registerProcessors(Collection<SpiderProcessor> processors){
        Assert.notNull(processors);
        for(SpiderProcessor processor : processors) {
            Assert.notNull(processor);
            Assert.notNull(processor.type());
            if (processorMap.containsKey(processor.type())) {
                throw new IllegalStateException("duplicate processor of type:" + processor.type().name());
            }
            processorMap.put(processor.type(), processor);
            log.info("registered processor :" + processor.type().name());
        }
        //add default extract processor
        processorMap.putIfAbsent(ProcessorTypeEnum.EXTRACTOR, new ExtractorProcessor());
    }

    public void registerExtractors(Collection<SpiderExtractor> extractors){
        Assert.notNull(extractors);
        for(SpiderExtractor extractor : extractors) {
            Assert.notNull(extractor);
            Assert.notNull(extractor.type());
            if (extractorMap.containsKey(extractor.type())) {
                throw new IllegalStateException("duplicate extractor of type:" + extractor.type().name());
            }
            extractorMap.put(extractor.type(), extractor);
            log.info("registered extractor :" + extractor.type().name());
        }
    }

    public void checkProcessors(){
        for(ProcessorTypeEnum processorType : ProcessorTypeEnum.values()){
            if(!processorMap.containsKey(processorType)){
                throw new SpiderException("processorType " + processorType.name() + " has no processor registered!!");
            }
        }
    }

    public void checkExtractors(){
        for(ExtractorTypeEnum extractorType : ExtractorTypeEnum.values()){
            if(!extractorMap.containsKey(extractorType)){
                throw new SpiderException("extractorType " + extractorType.name() + " has no extractor registered!!");
            }
        }
    }

    public SpiderQueue<Wrapper<SpiderJob>> getSpiderJobQueue(){
        checkRunning();
        return spiderJobQueue;
    }

    public SpiderQueue<Wrapper<Object>> getStoreQueue(){
        checkRunning();
        return storeQueue;
    }

    public Map<ProcessorTypeEnum, SpiderProcessor> getProcessorMap(){
        checkRunning();
        return processorMap;
    }

    public SpiderExtractor getExtractor(ExtractorTypeEnum type) {
        SpiderExtractor extractor = extractorMap.get(type);
        if(null == extractor){
            throw new SpiderException("extractorType " + type.name() + " has no extractor registered!!");
        }
        return extractor;
    }

    public void checkRunning(){
        if(!isRunning()){
           throw new SpiderException("Spider is not running");
        }
    }
}
