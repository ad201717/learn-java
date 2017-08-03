package com.howe.learn.spider.spider.mafengwo;

import com.howe.learn.spider.basic.core.SpiderJob;
import com.howe.learn.spider.basic.core.platform.PlatformEnum;
import com.howe.learn.spider.basic.core.processor.ProcessorTypeEnum;
import com.howe.learn.spider.basic.core.support.Wrapper;
import com.howe.learn.spider.spider.AbstractRouteSpider;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * @Author Karl
 * @Date 2017/1/6 11:08
 */
@Component
public class MafengwoRouteSpider extends AbstractRouteSpider {

    @Override
    protected List<Wrapper<SpiderJob>> initJob() {
        SpiderJob newJob = new SpiderJob("http://www.mafengwo.cn/mdd/", ProcessorTypeEnum.MAFENGWO_DEST_INDEX);
        return Arrays.asList(Wrapper.wrap(newJob, PlatformEnum.MAFENGWO));
    }
}
