package com.howe.learn.common.codec;

import com.howe.learn.common.request.CalcRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * @Author Karl
 * @Date 2016/10/28 14:08
 */

/** calcReq:x + y**/
public class CalcRequestToStringEncoder extends MessageToMessageEncoder<CalcRequest> {
    private static final String PREFIX = "calcReq:";
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    protected void encode(ChannelHandlerContext ctx, CalcRequest request, List<Object> out) throws Exception {
        StringBuilder sb = new StringBuilder(PREFIX)
                .append(request.getReqId() + ":")
                .append(request.getX())
                .append(" ")
                .append(request.getCalMethod().getValue())
                .append(" ")
                .append(request.getY())
                .append(LINE_SEPARATOR);
//        ByteBuf byteBuf = ctx.alloc().ioBuffer(sb.length());
//        byteBuf.writeBytes(sb.toString().getBytes());
//        out.add(byteBuf);
        out.add(sb.toString());
    }

}
