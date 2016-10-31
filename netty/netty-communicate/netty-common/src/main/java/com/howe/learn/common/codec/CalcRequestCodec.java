package com.howe.learn.common.codec;

import com.howe.learn.common.request.CalcRequest;
import com.howe.learn.common.response.CalcResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.List;

/**
 * @Author Karl
 * @Date 2016/10/28 14:08
 */
/** calcReq:x + y**/
public class CalcRequestCodec extends MessageToMessageCodec<String, CalcRequest> {
    private static final String PREFIX = "calcReq:";
    protected void encode(ChannelHandlerContext ctx, CalcRequest request, List<Object> out) throws Exception {
        StringBuilder sb = new StringBuilder(PREFIX)
                .append(request.getReqId() + ":")
                .append(request.getX())
                .append(" ")
                .append(request.getCalMethod().getValue())
                .append(" ")
                .append(request.getY());
//        ByteBuf byteBuf = ctx.alloc().ioBuffer(sb.length());
//        byteBuf.writeBytes(sb.toString().getBytes());
//        out.add(byteBuf);
        out.add(sb.toString());
    }

    protected void decode(ChannelHandlerContext ctx, String msg, List<Object> out) throws Exception {
        if(msg.startsWith(PREFIX)){
            msg = msg.substring(PREFIX.length() - 1);
            String[] arrs = msg.split(":");
            CalcRequest request = new CalcRequest();
            request.setReqId(arrs[0]);
            request.setX(Integer.parseInt(arrs[1].split(" ")[0]));
            request.setCalMethod(CalcRequest.CalMethod.from(arrs[1].split(" ")[1].toCharArray()[0]));
            request.setY(Integer.parseInt(arrs[1].split(" ")[2]));
            out.add(request);
        } else {
            throw new Exception("unknown msg:" + msg);
        }
    }
}
