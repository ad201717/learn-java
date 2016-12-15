package com.howe.learn.quartz;

import java.util.Date;

/**
 * @Author Karl
 * @Date 2016/12/13 10:50
 */
public class HelloWorldTask{

    public void run(){
        String msg = "hello world from " + Thread.currentThread().getId() + " at:" + new Date();
        System.out.println(msg);
    }
}
