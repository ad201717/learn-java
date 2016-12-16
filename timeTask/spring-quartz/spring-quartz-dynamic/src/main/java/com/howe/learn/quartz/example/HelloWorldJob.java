package com.howe.learn.quartz.example;

import com.howe.learn.quartz.DynamicJob;
import com.howe.learn.quartz.DynamicJobScheduler;
import com.howe.learn.quartz.util.SpringContextUtil;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author Karl
 * @Date 2016/12/16 17:18
 */
public class HelloWorldJob implements DynamicJob {

    private static final Logger log = LoggerFactory.getLogger(HelloWorldJob.class);

    private int index;

    private Map<String, Object> jobData = new HashMap<String, Object>();

    public HelloWorldJob(){

    }

    public HelloWorldJob(int index){
        this.index = index;
        this.getJobData().put("index", index);
    }

    @Override
    public String getKey() {
        return String.valueOf(index);
    }

    @Override
    public long getStartDelayInMills() {
        return 50000;
    }

    @Override
    public Map<String, Object> getJobData() {
        return jobData;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info(String.valueOf(context.getJobDetail().getJobDataMap().get("hello")));
        log.info(String.valueOf(context.getJobDetail().getJobDataMap().get("index")));

        DynamicJobScheduler dynamicJobScheduler = (DynamicJobScheduler) SpringContextUtil.getBean("dynamicJobScheduler");
        dynamicJobScheduler.rmJob(context.getJobDetail().getKey().getName());
    }
}
