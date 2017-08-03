package com.howe.learn.spider.spider.mafengwo.processor;

import com.howe.learn.spider.basic.cache.BlacklistCache;
import com.howe.learn.spider.basic.core.Collector;
import com.howe.learn.spider.basic.core.SpiderJob;
import com.howe.learn.spider.basic.core.processor.ProcessorTypeEnum;
import com.howe.learn.spider.basic.core.processor.SpiderProcessor;
import com.howe.learn.spider.basic.core.support.Wrapper;
import com.howe.learn.spider.basic.domain.Route;
import com.howe.learn.spider.util.DocumentUtil;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @Author Karl
 * @Date 2017/1/6 11:20
 */
@Component
public class MafengwoCityPageProcessor implements SpiderProcessor {

    private static final Logger log = LoggerFactory.getLogger(MafengwoCityPageProcessor.class);

    @Override
    public ProcessorTypeEnum type() {
        return ProcessorTypeEnum.MAFENGWO_CITY_PAGE;
    }

    @Override
    public void process(Wrapper<SpiderJob> spiderJobWrapper
            , Collector<Wrapper<Object>> storeCollector, Collector<Wrapper<SpiderJob>> spiderJobCollector) {
        SpiderJob spiderJob = spiderJobWrapper.getObj();
        if(BlacklistCache.getInstance().exists(spiderJob.getUrl())){
            System.out.println("skip no-route cityUrl:" + spiderJob.getUrl());
            return;
        }
        try {
            Document doc = DocumentUtil.parse(spiderJob.getUrl());
            Element routeElement = doc.select(".navbar>.navbar-line>a").first();
            if (null != routeElement && !StringUtil.isBlank(routeElement.absUrl("href"))) {
                String routeListUrl = routeElement.absUrl("href");
                SpiderJob newJob = new SpiderJob(routeListUrl, ProcessorTypeEnum.MAFENGWO_ROUTE_LIST);
                spiderJobCollector.collect(Wrapper.wrap(newJob, spiderJobWrapper.getPlatform()), 3000);
            } else {
                log.debug("find no-route cityUrl：" + spiderJob.getUrl());
                BlacklistCache.getInstance().add(spiderJob.getUrl());
            }
        } catch (Exception e) {
            log.error(Thread.currentThread().getName() + " " + e.getMessage(), e);
        }
    }
}
