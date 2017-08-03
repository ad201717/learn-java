package com.howe.learn.spider.spider.mafengwo.processor;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.howe.learn.spider.basic.core.Collector;
import com.howe.learn.spider.basic.core.SpiderJob;
import com.howe.learn.spider.basic.core.extractor.ExtractorTypeEnum;
import com.howe.learn.spider.basic.core.processor.ProcessorTypeEnum;
import com.howe.learn.spider.basic.core.processor.SpiderProcessor;
import com.howe.learn.spider.basic.core.support.Wrapper;
import com.howe.learn.spider.basic.domain.Route;
import com.howe.learn.spider.util.DocumentUtil;
import com.howe.learn.spider.util.HttpUtils;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author Karl
 * @Date 2017/1/6 11:20
 */
@Component
public class MafengwoRouteListProcessor implements SpiderProcessor {

    private static final Logger log = LoggerFactory.getLogger(MafengwoRouteListProcessor.class);

    @Override
    public ProcessorTypeEnum type() {
        return ProcessorTypeEnum.MAFENGWO_ROUTE_LIST;
    }

    @Override
    public void process(Wrapper<SpiderJob> spiderJobWrapper
            , Collector<Wrapper<Object>> storeCollector, Collector<Wrapper<SpiderJob>> spiderJobCollector) {
        SpiderJob spiderJob = spiderJobWrapper.getObj();
        try {
            Document doc = DocumentUtil.parse(spiderJob.getUrl());

            Elements emts = doc.select(".btn-more");
            for (int i = 0; i < emts.size(); i++) {
                String routeUrl = emts.get(i).absUrl("href");
                SpiderJob newJob = new SpiderJob(routeUrl, ExtractorTypeEnum.MAFENGWO);
                spiderJobCollector.collect(Wrapper.wrap(newJob, spiderJobWrapper.getPlatform()), 3000);
            }

            String pageStr = doc.select("#routelistpagination>.m-pagination>.count").text().replaceAll("[共页]", "");
            int pages = StringUtil.isBlank(pageStr) ? 0 : Integer.parseInt(pageStr);

            for (int i = 2; i <= pages; i++) {
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("mddid", spiderJob.getUrl().substring(spiderJob.getUrl().lastIndexOf('/') + 1).replaceAll(".html", ""));
                params.put("page", i);
                params.put("type", 2);

                String resp = HttpUtils.post("http://www.mafengwo.cn/mdd/base/routeline/pagedata_routelist", params);
                Map<String, Object> map = new GsonBuilder().create().fromJson(resp, new TypeToken<Map<String, Object>>(){}.getType());

                Document d = Jsoup.parse(map.get("list").toString());
                d.setBaseUri(doc.baseUri());
                emts = d.select(".btn-more");

                for (int k = 0; k < emts.size(); k++) {
                    String routeUrl = emts.get(k).absUrl("href");
                    SpiderJob newJob = new SpiderJob(routeUrl, ExtractorTypeEnum.MAFENGWO);
                    spiderJobCollector.collect(Wrapper.wrap(newJob, spiderJobWrapper.getPlatform()), 3000);
                }
            }
        } catch (Exception e) {
            log.error(Thread.currentThread().getName() + " " + e.getMessage(), e);
        }
    }
}
