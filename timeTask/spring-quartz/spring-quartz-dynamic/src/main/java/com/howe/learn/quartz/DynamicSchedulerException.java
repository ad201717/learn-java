package com.howe.learn.quartz;

/**
 * @Author Karl
 * @Date 2016/12/15 17:06
 */
public class DynamicSchedulerException extends RuntimeException {

    public DynamicSchedulerException(String msg){
        super(msg);
    }

    public DynamicSchedulerException(String msg, Throwable e){
        super(msg, e);
    }
}
