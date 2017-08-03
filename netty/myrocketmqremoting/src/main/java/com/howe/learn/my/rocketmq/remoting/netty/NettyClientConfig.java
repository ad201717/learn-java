package com.howe.learn.my.rocketmq.remoting.netty;

/**
 * @Author Karl
 * @Date 2017/4/12 16:28
 */
public class NettyClientConfig {

    public final static int clientWorkerThreads = 4;
    public final static int clientSemaphoreAsyncValue = 2048;
    public final static int clientSemaphoreOnewayValue = 2048;
    public final static int clientConnectTimeoutMills = 3000;
}
