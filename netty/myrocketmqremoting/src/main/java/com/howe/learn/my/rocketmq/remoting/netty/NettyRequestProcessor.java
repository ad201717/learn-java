package com.howe.learn.my.rocketmq.remoting.netty;

import com.howe.learn.my.rocketmq.remoting.exception.RemotingException;
import com.howe.learn.my.rocketmq.remoting.protocol.RemotingCommand;
import io.netty.channel.ChannelHandlerContext;

/**
 * @Author Karl
 * @Date 2017/4/11 9:53
 */
public interface NettyRequestProcessor {

    RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) throws RemotingException;
}
