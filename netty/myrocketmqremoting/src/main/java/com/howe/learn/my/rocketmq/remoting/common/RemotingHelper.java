package com.howe.learn.my.rocketmq.remoting.common;

import com.howe.learn.my.rocketmq.remoting.netty.NettyClient;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;

/**
 * @Author Karl
 * @Date 2017/4/11 15:57
 */
public class RemotingHelper {

    public static final Logger log = LoggerFactory.getLogger("rocket-remoting");

    public static String parseRemoteAddress(Channel channel) {
        if (null == channel) {
            return "";
        }
        SocketAddress address = channel.remoteAddress();
        final String addr = null != address ? address.toString() : "";

        if (addr.length() > 0) {
            int index = addr.lastIndexOf("/");
            if(index > -1) {
                return addr.substring(index + 1);
            }
        }
        return addr;
    }

    public static void closeChannel(Channel channel) {
        final String addr = parseRemoteAddress(channel);
        channel.close().addListener((f)->{
            log.info("close channel<{}> result:{}", addr, f.isSuccess());
        });
    }
}
