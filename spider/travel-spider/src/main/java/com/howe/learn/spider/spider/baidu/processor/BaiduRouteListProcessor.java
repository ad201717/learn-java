package com.howe.learn.spider.spider.baidu.processor;

import com.howe.learn.spider.basic.core.Collector;
import com.howe.learn.spider.basic.core.SpiderJob;
import com.howe.learn.spider.basic.core.extractor.ExtractorTypeEnum;
import com.howe.learn.spider.basic.core.platform.PlatformEnum;
import com.howe.learn.spider.basic.core.processor.ProcessorTypeEnum;
import com.howe.learn.spider.basic.core.processor.SpiderProcessor;
import com.howe.learn.spider.basic.core.support.Wrapper;
import com.howe.learn.spider.basic.domain.Route;
import com.howe.learn.spider.util.DocumentUtil;
import com.howe.learn.spider.util.URLUtils;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.regex.Pattern;

/**
 * 百度行程列表页处理器
 *
 * @Author Karl
 * @Date 2017/1/5 10:32
 */
@Component
public class BaiduRouteListProcessor implements SpiderProcessor {
    private static final Logger log = LoggerFactory.getLogger(BaiduRouteListProcessor.class);

    private ThreadLocal<Pattern> routeDetailPattern = new ThreadLocal<Pattern>(){
        @Override
        protected Pattern initialValue() {
            return Pattern.compile("https://lvyou\\.baidu\\.com/plan/[0-9a-z]+");
        }
    };

    @Override
    public ProcessorTypeEnum type() {
        return ProcessorTypeEnum.BAIDU_LIST;
    }

    @Override
    public void process(Wrapper<SpiderJob> spiderJobWrapper
            , Collector<Wrapper<Object>> routeCollector
            , Collector<Wrapper<SpiderJob>> spiderJobCollector) {
        SpiderJob spiderJob = spiderJobWrapper.getObj();
        log.info(Thread.currentThread().getName() + " start process job:" + spiderJob + " from platform:" + spiderJobWrapper.getPlatform());
        try {
            Document doc = DocumentUtil.parse(spiderJob.getUrl());
            this.parseRouteDetailUrl(doc, spiderJobCollector, spiderJobWrapper.getPlatform());
            this.parseRouteListUrl(doc, spiderJobCollector, URLUtils.truncateParam(spiderJob.getUrl()), spiderJobWrapper.getPlatform());
        } catch (Exception e) {
            log.error(Thread.currentThread().getName() + " start process job:" + spiderJob + ", error", e);
        }
    }

    private void parseRouteDetailUrl(Document doc, Collector<Wrapper<SpiderJob>> spiderJobCollector, PlatformEnum platform){
        Iterator<Element> elements = doc.select(".plan-list .good-container #pagelet_list .list-item>a").iterator();
        while(elements.hasNext()){
            String url = URLUtils.truncateParam(elements.next().absUrl("href"));
            if(routeDetailPattern.get().matcher(url).matches()) {
                SpiderJob newJob = new SpiderJob(url, ProcessorTypeEnum.EXTRACTOR);
                newJob.setExtractorType(ExtractorTypeEnum.BAIDU);
                if(log.isDebugEnabled()) {
                    log.debug(Thread.currentThread().getName() + " collect new job:" + newJob);
                }
                spiderJobCollector.collect(Wrapper.wrap(newJob, platform), 3000);
            } else {
                if(log.isWarnEnabled()) {
                    log.warn(Thread.currentThread().getName() + " find unmatched url:" + url);
                }
            }
        }
    }

    private void parseRouteListUrl(Document doc, Collector<Wrapper<SpiderJob>> spiderJobCollector, String baseUrl,
                                   PlatformEnum platform) {
        Iterator<Element> elements = doc.select(".pagelist-wrapper .pagelist a").iterator();
        while(elements.hasNext()){
            Element ele = elements.next();
            if(!StringUtil.isNumeric(ele.text())){
                continue;
            }
            String url = baseUrl + ele.attr("href");
            SpiderJob newJob = new SpiderJob(url, ProcessorTypeEnum.BAIDU_LIST);
            if(log.isDebugEnabled()) {
                log.debug(Thread.currentThread().getName() + " collect new job:" + newJob);
            }
            spiderJobCollector.collect(Wrapper.wrap(newJob, platform), 3000);
        }
    }
}
