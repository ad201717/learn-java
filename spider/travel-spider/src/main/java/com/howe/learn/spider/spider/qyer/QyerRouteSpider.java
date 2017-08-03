package com.howe.learn.spider.spider.qyer;

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
 * @Date 2017/1/4 16:42
 */
@Component
public class QyerRouteSpider extends AbstractRouteSpider{

    @Override
    protected List<Wrapper<SpiderJob>> initJob() {
        SpiderJob newJob = new SpiderJob("http://plan.qyer.com/", ProcessorTypeEnum.QYER_CITY_LIST);
        return Arrays.asList(Wrapper.wrap(newJob, PlatformEnum.QYER));
    }
}
