package com.howe.learn.quartz;

import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author Karl
 * @Date 2016/12/15 16:42
 */
public class DynamicJobScheduler {

    private static final Logger log = LoggerFactory.getLogger(DynamicJobScheduler.class);

    private Scheduler scheduler;
    private AtomicInteger state = new AtomicInteger(0);
    private static final int STATE_NOT_RUNNING = 0;
    private static final int STATE_RUNNING     = 1;
    private static final int STATE_STOPED      = 2;


    public void stop(){
        if(state.get() == STATE_STOPED){
            return;
        }
        if(state.get() == STATE_NOT_RUNNING){
            throw new DynamicSchedulerException("dynamicJobScheduler 未启动");
        }
        if(state.compareAndSet(STATE_RUNNING, STATE_STOPED)){
            try {
                scheduler.shutdown(true);
            } catch (SchedulerException e) {
                log.error("dynamicJobScheduler 启动异常", e);
                throw new DynamicSchedulerException("dynamicJobScheduler 启动异常", e);
            }
        }
    }

    public void start(){
        if(state.get() == STATE_RUNNING){
            return;
        }
        if(state.get() == STATE_STOPED){
            throw new DynamicSchedulerException("dynamicJobScheduler 已经停止");
        }
        if(state.compareAndSet(STATE_NOT_RUNNING, STATE_RUNNING)){
            try {
                scheduler.start();
            } catch (SchedulerException e) {
                log.error("dynamicJobScheduler 启动异常", e);
                throw new DynamicSchedulerException("dynamicJobScheduler 启动异常", e);
            }
        }
    }

    public <T extends DynamicJob> void  schedulerNewJob(T job){
        try {
            JobKey jobKey = JobKey.jobKey(job.getKey());
            if(scheduler.checkExists(TriggerKey.triggerKey(job.getKey()))) {
                log.error("已存在任务计划,任务key:" + job.getKey());
                throw new DynamicSchedulerException("已存在任务计划,任务key:" + job.getKey());
            }

            JobDataMap jobDataMap = new JobDataMap(job.getJobData());
            JobDetail jobDetail = JobBuilder.newJob(job.getClass())
                    .setJobData(jobDataMap)
                    .withIdentity(jobKey)
                    .build();

            SimpleTriggerFactoryBean trigger = new SimpleTriggerFactoryBean();
            trigger.setName("trigger-" + job.getKey());
            trigger.setJobDetail(jobDetail);
            trigger.setStartDelay(job.getStartDelayInMills());
            trigger.setRepeatInterval(30000);
            trigger.afterPropertiesSet();

            Date date = scheduler.scheduleJob(jobDetail, trigger.getObject());

            if(!scheduler.isStarted()){
                scheduler.start();
            }

            log.info(date.toString());
        } catch (SchedulerException e) {
            log.error("检查任务计划失败,任务key:" + job.getKey());
            throw new DynamicSchedulerException("检查任务计划失败,任务key:" + job.getKey(), e);
        } catch (ParseException e) {
            log.error("创建trigger失败" + job.getKey(), e);
            throw new DynamicSchedulerException("创建trigger失败" + job.getKey(), e);
        }
    }

    public void rmJob(String key){
        try {
            boolean deleted = scheduler.deleteJob(JobKey.jobKey(key));
            log.info("删除任务计划{},结果:{}", key, deleted);
        } catch (SchedulerException e) {
            log.error("删除任务计划失败,任务key:" + key);
            throw new DynamicSchedulerException("删除任务计划失败,任务key:" + key, e);
        }
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

}
