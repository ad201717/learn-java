package com.howe.learn.spider.spider.qyer.processor;

import com.howe.learn.spider.basic.core.Collector;
import com.howe.learn.spider.basic.core.SpiderJob;
import com.howe.learn.spider.basic.core.extractor.ExtractorTypeEnum;
import com.howe.learn.spider.basic.core.platform.PlatformEnum;
import com.howe.learn.spider.basic.core.processor.ProcessorTypeEnum;
import com.howe.learn.spider.basic.core.processor.SpiderProcessor;
import com.howe.learn.spider.basic.core.support.Wrapper;
import com.howe.learn.spider.util.DocumentUtil;
import com.howe.learn.spider.util.PropertiesUtil;
import com.howe.learn.spider.util.URLUtils;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Iterator;

/**
 * @Author Karl
 * @Date 2017/1/9 16:25
 */
@Component
public class QyerRouteListProcessor implements SpiderProcessor {

    private static final Logger log = LoggerFactory.getLogger(QyerRouteListProcessor.class);

    private static final int DEFAULT_LEAST_COPY = 10;

    private static int LEAST_COPY = DEFAULT_LEAST_COPY;

    private static final String LEAST_COPDY_KEY = "qyer.least.copy";

    static {
        if(StringUtil.isNumeric(PropertiesUtil.get(LEAST_COPDY_KEY))){
            LEAST_COPY = Integer.parseInt(PropertiesUtil.get(LEAST_COPDY_KEY));
        }
    }

    @Override
    public ProcessorTypeEnum type() {
        return ProcessorTypeEnum.QYER_LIST;
    }

    @Override
    public void process(Wrapper<SpiderJob> spiderJobWrapper, Collector<Wrapper<Object>> storeCollector, Collector<Wrapper<SpiderJob>> spiderJobCollector) {
        SpiderJob spiderJob = spiderJobWrapper.getObj();
        log.info(Thread.currentThread().getName() + " start process job:" + spiderJob + " from platform:" + spiderJobWrapper.getPlatform());
        try {
            Document doc = DocumentUtil.parse(spiderJob.getUrl());
            boolean nextPage = this.parseRouteDetailUrl(doc, spiderJobCollector, spiderJobWrapper.getPlatform());
            if(nextPage) {
                log.debug(Thread.currentThread().getName() + " continue crawl next page");
                this.parseRouteListUrl(doc, spiderJobCollector, URLUtils.truncateParam(spiderJob.getUrl()), spiderJobWrapper.getPlatform());
            }
        } catch (Exception e) {
            log.error(Thread.currentThread().getName() + " start process job:" + spiderJob + ", error", e);
        }
    }

    private boolean parseRouteDetailUrl(Document doc, Collector<Wrapper<SpiderJob>> spiderJobCollector, PlatformEnum platform) {
        Iterator<Element> elements = doc.select(".lists>.list>.items").iterator();
        while (elements.hasNext()) {
            Element ele = elements.next();
            if(Integer.parseInt(ele.select(".content>.number>.icon2").text()) < LEAST_COPY){
                return false;
            }
            String url = ele.select("a:eq(0)").first().absUrl("href");
            SpiderJob newJob = new SpiderJob(url, ProcessorTypeEnum.EXTRACTOR);
            newJob.setExtractorType(ExtractorTypeEnum.QYER);
            if (log.isDebugEnabled()) {
                log.debug(Thread.currentThread().getName() + " collect new job:" + newJob);
            }
            spiderJobCollector.collect(Wrapper.wrap(newJob, platform), 3000);
        }
        return true;
    }

    private void parseRouteListUrl(Document doc, Collector<Wrapper<SpiderJob>> spiderJobCollector, String baseUrl,
                                   PlatformEnum platform) {
        int current = Integer.parseInt(doc.select(".pages>.ui_page>.ui_page_item_current").text());
        Elements nextPageEle = doc.select(".pages>.ui_page>a.ui_page_item[data-page=" + (current + 1) + "]");
        if(!nextPageEle.isEmpty()){
            String url = nextPageEle.first().absUrl("href");
            SpiderJob newJob = new SpiderJob(url, ProcessorTypeEnum.QYER_LIST);
            if (log.isDebugEnabled()) {
                log.debug(Thread.currentThread().getName() + " collect new job:" + newJob);
            }
            spiderJobCollector.collect(Wrapper.wrap(newJob, platform), 3000);
        }
//        Iterator<Element> elements = doc.select(".pages>.ui_page>a").iterator();
//        while (elements.hasNext()) {
//            Element ele = elements.next();
//            if (!StringUtil.isNumeric(ele.text())) {
//                continue;
//            }
//            String url = ele.absUrl("href");
//            SpiderJob newJob = new SpiderJob(url, ProcessorTypeEnum.QYER_LIST);
//            if (log.isDebugEnabled()) {
//                log.debug(Thread.currentThread().getName() + " collect new job:" + newJob);
//            }
//            spiderJobCollector.collect(Wrapper.wrap(newJob, platform), 3000);
//        }
    }
}
