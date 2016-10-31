package com.howe.learn.common.codec;

import com.howe.learn.common.request.CalcRequest;
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
public class CalcResponseCodec extends MessageToMessageCodec<String, CalcResponse> {
    private static final String PREFIX = "calcResp:";
    protected void encode(ChannelHandlerContext ctx, CalcResponse response, List<Object> out) throws Exception {
        StringBuilder sb = new StringBuilder(PREFIX)
                .append(response.getReqId())
                .append(":")
                .append(response.getReqStr())
                .append("=")
                .append(response.getZ());
        ByteBuf byteBuf = ctx.alloc().ioBuffer(sb.length());
        byteBuf.writeBytes(sb.toString().getBytes());
        out.add(byteBuf);
    }

    protected void decode(ChannelHandlerContext ctx, String msg, List<Object> out) throws Exception {
        if(msg.startsWith(PREFIX)){
            msg = msg.substring(PREFIX.length() - 1);
            CalcResponse response = new CalcResponse();
            response.setReqId(msg.split(":")[0]);
            response.setReqStr(msg.split(":")[1].split("=")[0]);
            response.setZ(Integer.parseInt(msg.split(":")[1].split("=")[1]));
            out.add(response);
        } else {
            throw new Exception("unknown msg:" + msg);
        }
    }
}
