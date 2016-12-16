package com.howe.learn.quartz.example;

import com.howe.learn.quartz.DynamicJob;
import com.howe.learn.quartz.DynamicJobScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 动态生成定时任务
 *
 * @Author Karl
 * @Date 2016/12/15 16:34
 */
public class DynamicJobCreator {
    private static final Logger log = LoggerFactory.getLogger(DynamicJobCreator.class);
    /**
     * 动态创建任务次数
     */
    private int total = 5;
    /**
     * 任务间隔时间(ms)
     */
    private int initWaitTime = 10000;

    private DynamicJobScheduler dynamicJobScheduler;

    public void init(){
        new Thread(()-> {
                int index = 0;
                while(index++ < total){
                    createJob(index);
                    initWaitTime *= (int) Math.pow(2, index);
                    try {
                        Thread.sleep(initWaitTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
        }).start();
    }

    private void createJob(final int index){
        log.info("create new dynamic job-" + index);
        DynamicJob job = new HelloWorldJob(index);
        job.getJobData().put("hello", "world");
        dynamicJobScheduler.schedulerNewJob(job);
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getInitWaitTime() {
        return initWaitTime;
    }

    public void setInitWaitTime(int initWaitTime) {
        this.initWaitTime = initWaitTime;
    }

    public DynamicJobScheduler getDynamicJobScheduler() {
        return dynamicJobScheduler;
    }

    public void setDynamicJobScheduler(DynamicJobScheduler dynamicJobScheduler) {
        this.dynamicJobScheduler = dynamicJobScheduler;
    }
}
