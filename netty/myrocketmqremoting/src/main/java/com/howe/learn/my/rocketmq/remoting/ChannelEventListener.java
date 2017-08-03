package com.howe.learn.my.rocketmq.remoting;

import io.netty.channel.Channel;

/**
 * @Author Karl
 * @Date 2017/4/11 15:05
 */
public interface ChannelEventListener {

    void onChannelConnect(String remoteAddress, Channel channel);

    void onChannelClose(String remoteAddress, Channel channel);

    void onChannelException(String remoteAddress, Channel channel);

    void onChannelIdle(String remoteAddress, Channel channel);
}
