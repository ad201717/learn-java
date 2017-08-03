package com.howe.learn.my.rocketmq.remoting.netty;

import com.howe.learn.my.rocketmq.remoting.common.RemotingHelper;
import com.howe.learn.my.rocketmq.remoting.protocol.RemotingCommand;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * @Author Karl
 * @Date 2017/4/13 10:25
 */
public class NettyDecoder extends LengthFieldBasedFrameDecoder {

    public NettyDecoder() {
        super(8388608, 0, 4, 0, 4);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = null;
        try {
            frame = (ByteBuf) super.decode(ctx, in);
            if (null == frame) {
                return null;
            }

            ByteBuffer buf = frame.nioBuffer();
            return RemotingCommand.decode(buf);
        } catch (Exception e) {
            RemotingHelper.log.error("decode error," + RemotingHelper.parseRemoteAddress(ctx.channel()), e);
            RemotingHelper.closeChannel(ctx.channel());
        } finally {
            if (null != frame) {
                frame.release();
            }
        }
        return null;
    }
}
