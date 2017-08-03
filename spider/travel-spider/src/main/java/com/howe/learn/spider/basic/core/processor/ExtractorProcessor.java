package com.howe.learn.spider.basic.core.processor;

import com.howe.learn.spider.basic.cache.BlacklistCache;
import com.howe.learn.spider.basic.core.Collector;
import com.howe.learn.spider.basic.core.Spider;
import com.howe.learn.spider.basic.core.SpiderJob;
import com.howe.learn.spider.basic.core.support.Wrapper;
import com.howe.learn.spider.basic.domain.Route;
import com.howe.learn.spider.util.DocumentUtil;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.howe.learn.spider.basic.core.processor.ProcessorTypeEnum.EXTRACTOR;

/**
 * @Author Karl
 * @Date 2017/1/5 18:59
 */
public class ExtractorProcessor implements SpiderProcessor {

    private static final Logger log = LoggerFactory.getLogger(ExtractorProcessor.class);

    @Override
    public ProcessorTypeEnum type() {
        return EXTRACTOR;
    }

    @Override
    public void process(Wrapper<SpiderJob> spiderJobWrapper, Collector<Wrapper<Object>> routeCollector, Collector<Wrapper<SpiderJob>> spiderJobCollector) {
        SpiderJob spiderJob = spiderJobWrapper.getObj();
        log.info(Thread.currentThread().getName() + " start process job:" + spiderJob + " from platform:" + spiderJobWrapper.getPlatform());
        try {
            if(BlacklistCache.getInstance().exists(spiderJob.getUrl())){
                return;
            }
            if(null == spiderJob.getExtractorType()){
                log.error("job without extractorType.. " + spiderJob);
            }
            Document doc = DocumentUtil.parse(spiderJob.getUrl());
            Object obj = Spider.getInstance().getExtractor(spiderJob.getExtractorType()).extract(doc, spiderJob.getUrl());
            if(null != obj){
                routeCollector.collect(Wrapper.wrap(obj, spiderJobWrapper.getPlatform()), 3000);
            }
        } catch (Exception e) {
            log.error(Thread.currentThread().getName() + " start process job:" + spiderJob + ", error:" + e.getMessage(), e);
        }
    }
}
