package com.howe.learn.spider.spider.mafengwo.extractor;

import com.howe.learn.spider.basic.core.extractor.ExtractorTypeEnum;
import com.howe.learn.spider.basic.core.extractor.SpiderExtractor;
import com.howe.learn.spider.basic.domain.Route;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

/**
 * @Author Karl
 * @Date 2017/1/6 11:45
 */
@Component
public class MafengwoRouteExtractor implements SpiderExtractor<Route> {
    @Override
    public ExtractorTypeEnum type() {
        return ExtractorTypeEnum.MAFENGWO;
    }

    @Override
    public Route extract(Document doc, String sourceUrl) {
        return extractRoute(doc, sourceUrl);
    }

    public Route extractRoute(Document doc, String sourceUrl) {
        Route route = new Route();
        route.setRouteName(doc.select("dl.line-summary>dt>h1").first().text().trim());
        route.setSourceUrl(sourceUrl);
        route.setDescr(doc.select("div.line-intro>div.intro-word>ul.notes>li.merit>p").text().trim());
        if (StringUtils.isBlank(route.getDescr())) {
            route.setDescr(doc.select("div.line-intro>div.intro-word>ul.notes>li.tips>p").text().trim());
        }

        return route;
    }

}
