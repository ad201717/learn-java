package com.howe.learn.my.rocketmq.remoting;

import com.howe.learn.my.rocketmq.remoting.exception.RemotingException;
import com.howe.learn.my.rocketmq.remoting.netty.NettyRequestProcessor;
import com.howe.learn.my.rocketmq.remoting.protocol.RemotingCommand;
import io.netty.channel.Channel;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * @Author Karl
 * @Date 2017/4/11 9:50
 */
public interface RemotingClient extends RemotingService {

    void registerProcessor(int requestCode, NettyRequestProcessor processor, ExecutorService executorService);

//    void updateNameServerList(List<String> nameServerList);
//
//    List<String> getNameServerList();

    boolean isChannelWritable(String addr);

    RemotingCommand invokeSync(String address, RemotingCommand request, long timeoutMills) throws RemotingException, InterruptedException;

    void invokeAsync(String address, RemotingCommand request, long timeoutMills, InvokeCallback invokeCallback) throws RemotingException, InterruptedException;

    void invokeOneway(String address, RemotingCommand request, long timeoutMills) throws RemotingException, InterruptedException;
}
