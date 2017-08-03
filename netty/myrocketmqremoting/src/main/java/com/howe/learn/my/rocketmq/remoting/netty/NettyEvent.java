package com.howe.learn.my.rocketmq.remoting.netty;

import io.netty.channel.Channel;

/**
 * @Author Karl
 * @Date 2017/4/11 15:15
 */
public class NettyEvent {
    private final NettyEventType eventType;

    private final String remoteAddress;

    private final Channel channel;

    public NettyEvent(NettyEventType eventType, String remoteAddress, Channel channel) {
        this.eventType = eventType;
        this.remoteAddress = remoteAddress;
        this.channel = channel;
    }

    public NettyEventType getEventType() {
        return eventType;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public Channel getChannel() {
        return channel;
    }
}
