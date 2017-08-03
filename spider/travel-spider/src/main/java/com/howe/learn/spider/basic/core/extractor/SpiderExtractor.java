package com.howe.learn.spider.basic.core.extractor;

import com.howe.learn.spider.basic.domain.Route;
import org.jsoup.nodes.Document;

/**
 * @Author Karl
 * @Date 2017/1/5 16:46
 */
public interface SpiderExtractor<T> {

    ExtractorTypeEnum type();

    T extract(Document doc, String sourceUrl);

}
