package com.howe.learn.spider.spider.mafengwo.processor;

import com.howe.learn.spider.basic.core.Collector;
import com.howe.learn.spider.basic.core.SpiderJob;
import com.howe.learn.spider.basic.core.processor.ProcessorTypeEnum;
import com.howe.learn.spider.basic.core.processor.SpiderProcessor;
import com.howe.learn.spider.basic.core.support.Wrapper;
import com.howe.learn.spider.basic.domain.Route;
import com.howe.learn.spider.util.DocumentUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Iterator;

/**
 * @Author Karl
 * @Date 2017/1/6 11:20
 */
@Component
public class MafengwoDestIndexProcessor implements SpiderProcessor {

    private static final Logger log = LoggerFactory.getLogger(MafengwoDestIndexProcessor.class);

    @Override
    public ProcessorTypeEnum type() {
        return ProcessorTypeEnum.MAFENGWO_DEST_INDEX;
    }

    @Override
    public void process(Wrapper<SpiderJob> spiderJobWrapper, Collector<Wrapper<Object>> routeCollector, Collector<Wrapper<SpiderJob>> spiderJobCollector) {
        SpiderJob spiderJob = spiderJobWrapper.getObj();
        try {
            Document doc = DocumentUtil.parse(spiderJob.getUrl());
            Iterator<Element> iter = doc.select(".row-list .item li a").iterator();
            while (iter.hasNext()) {
                String cityUrl = iter.next().absUrl("href");
                SpiderJob newJob = new SpiderJob(cityUrl, ProcessorTypeEnum.MAFENGWO_CITY_PAGE);
                spiderJobCollector.collect(Wrapper.wrap(newJob, spiderJobWrapper.getPlatform()), 3000);
            }
        } catch (Exception e) {
            log.error(Thread.currentThread().getName() + " " + e.getMessage(), e);
        }
    }
}
