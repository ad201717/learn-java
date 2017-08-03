package com.howe.learn.spider.spider.qyer.extractor;

import java.util.regex.Pattern;

import com.howe.learn.spider.basic.core.extractor.ExtractorTypeEnum;
import com.howe.learn.spider.basic.core.extractor.SpiderExtractor;
import com.howe.learn.spider.basic.domain.Route;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

/**
 * @Author Karl
 * @Date 2017/1/9 17:22
 */
@Component
public class QyerRouteExtractor implements SpiderExtractor<Route> {

    private ThreadLocal<Pattern> scenicPat = new ThreadLocal<Pattern>(){
        @Override
        protected Pattern initialValue() {
            return Pattern.compile("http://place\\.qyer\\.com/poi/[0-9a-zA-Z\\-_]+/");
        }
    };

    @Override
    public ExtractorTypeEnum type() {
        return ExtractorTypeEnum.QYER;
    }

    @Override
    public Route extract(Document doc, String sourceUrl) {
        return extractRoute(doc, sourceUrl);
    }

    public Route extractRoute(Document doc, String sourceUrl) {
        Route route = new Route();
        route.setRouteName(doc.select(".basic-info .title_txt").text());
        route.setSourceUrl(sourceUrl);

        return route;
    }

}
