package com.howe.learn.spider.basic.core.processor;

import com.howe.learn.spider.basic.core.Collector;
import com.howe.learn.spider.basic.core.SpiderJob;
import com.howe.learn.spider.basic.core.support.Wrapper;
import com.howe.learn.spider.basic.domain.Route;

/**
 * 爬虫任务处理器
 *
 * @Author Karl
 * @Date 2017/1/4 17:01
 */
public interface SpiderProcessor {

    ProcessorTypeEnum type();

    /**
     * 处理spiderJob，将抓取的结果写入routeCollector，将产生的新抓取任务写入spiderJobCollector
     * @param spiderJobWrapper
     * @param spiderJobCollector
     */
    void process(Wrapper<SpiderJob> spiderJobWrapper
            , Collector<Wrapper<Object>> storeCollector
            , Collector<Wrapper<SpiderJob>> spiderJobCollector);
}
