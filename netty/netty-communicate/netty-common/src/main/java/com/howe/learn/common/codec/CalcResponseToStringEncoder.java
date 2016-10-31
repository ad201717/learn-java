package com.howe.learn.common.codec;

import com.howe.learn.common.response.CalcResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * @Author Karl
 * @Date 2016/10/28 14:08
 */
public class CalcResponseToStringEncoder extends MessageToMessageEncoder<CalcResponse> {
    private static final String PREFIX = "calcResp:";
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    protected void encode(ChannelHandlerContext ctx, CalcResponse response, List<Object> out) throws Exception {
        StringBuilder sb = new StringBuilder(PREFIX)
                .append(response.getReqId())
                .append(":")
                .append(response.getReqStr())
                .append("=")
                .append(response.getZ())
                .append(LINE_SEPARATOR);
        ByteBuf byteBuf = ctx.alloc().ioBuffer(sb.length());
        byteBuf.writeBytes(sb.toString().getBytes());
        out.add(byteBuf);
    }

}
