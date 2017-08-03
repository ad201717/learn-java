package com.howe.learn.spider.spider.qyer.extractor;

import com.howe.learn.spider.basic.core.extractor.ExtractorTypeEnum;
import com.howe.learn.spider.basic.core.extractor.SpiderExtractor;
import com.howe.learn.spider.basic.domain.ScenicSpot;
import com.howe.learn.spider.util.DocumentUtil;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @Author Karl
 * @Date 2017/1/10 10:50
 */
@Component
public class QyerScenicExtractor implements SpiderExtractor<ScenicSpot> {
    @Override
    public ExtractorTypeEnum type() {
        return ExtractorTypeEnum.QYER_SCENIC;
    }

    @Override
    public ScenicSpot extract(Document doc, String sourceUrl) {
        return extractScenic(doc, sourceUrl);
    }

    public ScenicSpot extractScenic(Document doc, String sourceUrl){
        ScenicSpot scenicSpot = new ScenicSpot();
        scenicSpot.setScenicName(doc.select(".poi-top .poi-largeTit>.cn>a").text());
        scenicSpot.setDescr(doc.select(".poi-main .poi-detail>.poi-showtar>p").text());
        scenicSpot.setAddress(doc.select(".poi-main .poi-tips>li:eq(0)>.content>p").text().replace("(查看地图)", ""));
        if(!StringUtils.isEmpty(scenicSpot.getAddress()) && scenicSpot.getAddress().length() > 100){
            scenicSpot.setAddress(scenicSpot.getAddress().substring(0, 100));
        }
        scenicSpot.setSourceUrl(sourceUrl);
        return scenicSpot;
    }

    public static void main(String[] args) throws Exception {
        String url = "http://place.qyer.com/poi/V2AJZ1FuBz9TZA/";
        ScenicSpot scenicSpot = new QyerScenicExtractor().extractScenic(DocumentUtil.parse(url), url);
        System.out.println(scenicSpot);
    }
}
