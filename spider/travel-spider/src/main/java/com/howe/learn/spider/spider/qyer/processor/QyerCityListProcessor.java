package com.howe.learn.spider.spider.qyer.processor;

import com.howe.learn.spider.basic.core.Collector;
import com.howe.learn.spider.basic.core.SpiderJob;
import com.howe.learn.spider.basic.core.extractor.ExtractorTypeEnum;
import com.howe.learn.spider.basic.core.platform.PlatformEnum;
import com.howe.learn.spider.basic.core.processor.ProcessorTypeEnum;
import com.howe.learn.spider.basic.core.processor.SpiderProcessor;
import com.howe.learn.spider.basic.core.support.Wrapper;
import com.howe.learn.spider.util.DocumentUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Iterator;

/**
 * @Author Karl
 * @Date 2017/1/10 22:33
 */
@Component
public class QyerCityListProcessor implements SpiderProcessor {

    private static final Logger log = LoggerFactory.getLogger(QyerCityListProcessor.class);

    @Override
    public ProcessorTypeEnum type() {
        return ProcessorTypeEnum.QYER_CITY_LIST;
    }

    @Override
    public void process(Wrapper<SpiderJob> spiderJobWrapper, Collector<Wrapper<Object>> storeCollector, Collector<Wrapper<SpiderJob>> spiderJobCollector) {
        SpiderJob spiderJob = spiderJobWrapper.getObj();
        log.info(Thread.currentThread().getName() + " start process job:" + spiderJob + " from platform:" + spiderJobWrapper.getPlatform());
        try {
            Document doc = DocumentUtil.parse(spiderJob.getUrl());
            this.parseRouteListUrl(doc, spiderJobCollector, spiderJobWrapper.getPlatform());
        } catch (Exception e) {
            log.error(Thread.currentThread().getName() + " start process job:" + spiderJob + ", error", e);
        }
    }

    private void parseRouteListUrl(Document doc, Collector<Wrapper<SpiderJob>> spiderJobCollector, PlatformEnum platform) {
        Iterator<Element> elements = doc.select(".ind_sifter>.items>#js_placeList").first().select("a").iterator();
        while (elements.hasNext()) {
            String url = elements.next().absUrl("href");
            SpiderJob newJob = new SpiderJob(url + "&order=3", ProcessorTypeEnum.QYER_LIST);
            newJob.setExtractorType(ExtractorTypeEnum.QYER);
            if (log.isDebugEnabled()) {
                log.debug(Thread.currentThread().getName() + " collect new job:" + newJob);
            }
            spiderJobCollector.collect(Wrapper.wrap(newJob, platform), 3000);
        }
    }
}
