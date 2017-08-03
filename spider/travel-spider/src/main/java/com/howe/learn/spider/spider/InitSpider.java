package com.howe.learn.spider.spider;

import com.howe.learn.spider.basic.core.Spider;
import com.howe.learn.spider.basic.core.extractor.SpiderExtractor;
import com.howe.learn.spider.basic.core.processor.SpiderProcessor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.Map;

/**
 * @Author Karl
 * @Date 2017/1/5 10:01
 */
@Component
public class InitSpider implements InitializingBean, ApplicationContextAware{

    public static ApplicationContext applicationContext;

    public void init(){
        Map<String, SpiderProcessor> processorMap = applicationContext.getBeansOfType(SpiderProcessor.class);
        Spider.getInstance().registerProcessors(processorMap.values());

        Map<String, SpiderExtractor> extractorMap = applicationContext.getBeansOfType(SpiderExtractor.class);
        Spider.getInstance().registerExtractors(extractorMap.values());

//        Spider.getInstance().start();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        init();
    }

    @PreDestroy
    public void destory(){
        Spider.getInstance().shutdown(60 * 1000);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
