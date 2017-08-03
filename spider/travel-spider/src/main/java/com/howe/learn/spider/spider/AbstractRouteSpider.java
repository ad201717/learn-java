package com.howe.learn.spider.spider;

import com.howe.learn.spider.basic.core.Spider;
import com.howe.learn.spider.basic.core.SpiderJob;
import com.howe.learn.spider.basic.core.platform.PlatformEnum;
import com.howe.learn.spider.basic.core.support.Wrapper;
import com.howe.learn.spider.util.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;

/**
 * @Author Karl
 * @Date 2017/1/6 11:09
 */
public abstract class AbstractRouteSpider implements InitializingBean{
    private static final Logger log = LoggerFactory.getLogger(AbstractRouteSpider.class);

    private static final String ENABLED_PLATFORMS_KEY = "spider.enabled.platforms";

    public void init(){
        String enabledPlatform = PropertiesUtil.get(ENABLED_PLATFORMS_KEY);
        log.info("Spider enabled platforms : " + enabledPlatform);
        List<Wrapper<SpiderJob>> initJob = initJob();
        for(Wrapper<SpiderJob> job : initJob) {
            if(enabled(enabledPlatform, job.getPlatform())) {
                Spider.getInstance().newJob(job);
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        init();
    }

    protected abstract List<Wrapper<SpiderJob>> initJob();

    private boolean enabled(String enabledPlatform, PlatformEnum platform) {
        if("all".equals(enabledPlatform)
            || enabledPlatform.toLowerCase().indexOf(platform.name().toLowerCase()) > -1)
            return true;
        return false;
    }
}
