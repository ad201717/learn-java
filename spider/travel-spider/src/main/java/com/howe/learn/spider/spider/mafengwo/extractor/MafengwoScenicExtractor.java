package com.howe.learn.spider.spider.mafengwo.extractor;

import com.howe.learn.spider.basic.core.extractor.ExtractorTypeEnum;
import com.howe.learn.spider.basic.core.extractor.SpiderExtractor;
import com.howe.learn.spider.basic.domain.ScenicSpot;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

/**
 * Created by hao on 2017/1/7.
 */
@Component
public class MafengwoScenicExtractor implements SpiderExtractor<ScenicSpot> {
    @Override
    public ExtractorTypeEnum type() {
        return ExtractorTypeEnum.MAFENGWO_SCENIC;
    }

    @Override
    public ScenicSpot extract(Document doc, String sourceUrl) {
        return extractScenic(doc, sourceUrl);
    }

    public ScenicSpot extractScenic(Document doc, String sourceUrl){
        ScenicSpot scenicSpot = new ScenicSpot();
        scenicSpot.setScenicName(doc.select(".title>h1").text());
        scenicSpot.setDescr(doc.select(".summary").text());
        scenicSpot.setAddress(doc.select(".mod-location>.mhd>.sub").text());
        scenicSpot.setSourceUrl(sourceUrl);

        return scenicSpot;
    }

}
