package com.howe.learn.my.rocketmq.remoting.netty;

import com.howe.learn.my.rocketmq.remoting.common.RemotingHelper;
import com.howe.learn.my.rocketmq.remoting.protocol.RemotingCommand;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.ByteBuffer;

/**
 * @Author Karl
 * @Date 2017/4/13 10:25
 */
public class NettyEncoder extends MessageToByteEncoder<RemotingCommand> {
    @Override
    protected void encode(ChannelHandlerContext ctx, RemotingCommand msg, ByteBuf out) throws Exception {
        try {
            ByteBuffer buffer = msg.encode();
            out.writeBytes(buffer);
        } catch (Exception e) {
            RemotingHelper.log.error("encode response error," + RemotingHelper.parseRemoteAddress(ctx.channel()), e);
            if (null != msg) {
                RemotingHelper.log.error(msg.toString());
            }
            RemotingHelper.closeChannel(ctx.channel());
        }
    }
}
