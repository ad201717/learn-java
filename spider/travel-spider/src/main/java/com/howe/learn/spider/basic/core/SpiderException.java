package com.howe.learn.spider.basic.core;

/**
 * @Author Karl
 * @Date 2017/1/4 17:04
 */
public class SpiderException extends RuntimeException {

    public SpiderException(){
        super();
    }

    public SpiderException(String msg){
        super(msg);
    }

    public SpiderException(String msg, Throwable e) {
        super(msg, e);
    }
}
