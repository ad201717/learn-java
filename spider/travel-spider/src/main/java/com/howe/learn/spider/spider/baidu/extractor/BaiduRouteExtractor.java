package com.howe.learn.spider.spider.baidu.extractor;

import com.howe.learn.spider.basic.cache.BlacklistCache;
import com.howe.learn.spider.basic.core.extractor.ExtractorTypeEnum;
import com.howe.learn.spider.basic.core.extractor.SpiderExtractor;
import com.howe.learn.spider.basic.domain.Route;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @Author Karl
 * @Date 2017/1/5 16:52
 */
@Component
public class BaiduRouteExtractor implements SpiderExtractor{

    private static final Logger log = LoggerFactory.getLogger(BaiduRouteExtractor.class);

    @Override
    public ExtractorTypeEnum type() {
        return ExtractorTypeEnum.BAIDU;
    }

    @Override
    public Route extract(Document doc, String sourceUrl) {
        return extractRoute(doc, sourceUrl);
    }

    public Route extractRoute(Document doc, String sourceUrl) {
        Route route = new Route();
        Elements container = doc.select(".view-page-container");
        if(null == container || null == container.first()){
            log.info("find invalid route url:" + sourceUrl);
            BlacklistCache.getInstance().add(sourceUrl);
            return null;
        }
        route.setRouteName(container.select(".plan-info h1").attr("title"));
        route.setSourceUrl(sourceUrl);
        if(!doc.select(".expert-comment p.plan-comment").isEmpty()) {
            route.setDescr(doc.select(".expert-comment p.plan-comment").first().text().trim());
        }

        return route;
    }

}
