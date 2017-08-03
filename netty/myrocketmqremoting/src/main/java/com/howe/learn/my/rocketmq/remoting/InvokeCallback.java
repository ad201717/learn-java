package com.howe.learn.my.rocketmq.remoting;

import com.howe.learn.my.rocketmq.remoting.netty.ResponseFuture;

/**
 * @Author Karl
 * @Date 2017/4/11 10:00
 */
public interface InvokeCallback {

    void operationComplete(ResponseFuture future);
}
