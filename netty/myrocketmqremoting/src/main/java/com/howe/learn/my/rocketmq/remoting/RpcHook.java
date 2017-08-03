package com.howe.learn.my.rocketmq.remoting;

import com.howe.learn.my.rocketmq.remoting.protocol.RemotingCommand;

/**
 * @Author Karl
 * @Date 2017/4/11 9:32
 */
public interface RpcHook {

    void doBeforeRequest(String remoteAddress, RemotingCommand request);

    void doAfterResponse(String remoteAddress, RemotingCommand request, RemotingCommand response);
}
