package com.howe.learn.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Date;

/**
 * @Author Karl
 * @Date 2016/12/13 10:50
 */
public class AnotherTask implements Job{

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String msg = "Another task from " + Thread.currentThread().getId() + " at:" + new Date();
        System.out.println(msg);
    }
}
