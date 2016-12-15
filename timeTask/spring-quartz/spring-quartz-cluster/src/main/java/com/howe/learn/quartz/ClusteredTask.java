package com.howe.learn.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.util.Date;

/**
 * @Author Karl
 * @Date 2016/12/13 10:50
 */
public class ClusteredTask implements Job {

    private static final long serialVersionUID = -2741245392748358050L;

    public void execute(JobExecutionContext context){
        String msg = "clustered task from threadId:" + Thread.currentThread().getId() + " at:" + new Date();
        System.out.println(msg);
    }
}
