package com.howe.learn.spider.spider.baidu.extractor;

import com.howe.learn.spider.basic.core.extractor.ExtractorTypeEnum;
import com.howe.learn.spider.basic.core.extractor.SpiderExtractor;
import com.howe.learn.spider.basic.domain.ScenicSpot;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;


/**
 * Created by hao on 2017/1/7.
 */
@Component
public class BaiduScenicExtractor implements SpiderExtractor<ScenicSpot>{
    @Override
    public ExtractorTypeEnum type() {
        return ExtractorTypeEnum.BAIDU_SCENIC;
    }

    @Override
    public ScenicSpot extract(Document doc, String sourceUrl) {
        return extractScenic(doc, sourceUrl);
    }

    public ScenicSpot extractScenic(Document doc, String sourceUrl){
        ScenicSpot scenicSpot = new ScenicSpot();
        scenicSpot.setScenicName(doc.select(".main-name>a").text());
        scenicSpot.setDescr(doc.select(".main-info-more>p").text());
        scenicSpot.setAddress(doc.select(".more-info>.main-address p").text().replace("地址：", ""));
        scenicSpot.setSourceUrl(sourceUrl);

        return scenicSpot;
    }
}
