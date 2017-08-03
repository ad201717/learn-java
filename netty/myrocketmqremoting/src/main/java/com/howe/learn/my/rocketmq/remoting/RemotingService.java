package com.howe.learn.my.rocketmq.remoting;

/**
 * @Author Karl
 * @Date 2017/4/11 9:31
 */
public interface RemotingService {

    void start();

    void shutdown();

    void registerRpcHook(RpcHook rpcHook);
}
