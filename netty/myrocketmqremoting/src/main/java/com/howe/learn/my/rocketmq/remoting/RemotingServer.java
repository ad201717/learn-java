package com.howe.learn.my.rocketmq.remoting;

import com.howe.learn.my.rocketmq.remoting.exception.RemotingException;
import com.howe.learn.my.rocketmq.remoting.netty.NettyRequestProcessor;
import com.howe.learn.my.rocketmq.remoting.protocol.RemotingCommand;
import io.netty.channel.Channel;

import java.util.concurrent.ExecutorService;

/**
 * @Author Karl
 * @Date 2017/4/11 10:04
 */
public interface RemotingServer extends RemotingService {

    void registerProcessor(int requestCode, NettyRequestProcessor requestProcessor, ExecutorService executorService);

    void registerDefaultProcessor(NettyRequestProcessor requestProcessor, ExecutorService executorService);

    int localPort();

    RemotingCommand invokeSync(Channel channel, RemotingCommand request, long timeoutMills) throws RemotingException;

    void invokeAsync(Channel channel, RemotingCommand request, long timeoutMills, InvokeCallback invokeCallback) throws RemotingException;

    void invokeOneway(Channel channel, RemotingCommand request, long timeoutMills) throws RemotingException;
}
