package com.howe.learn.common.codec;

import com.howe.learn.common.response.CalcResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * @Author Karl
 * @Date 2016/10/28 14:08
 */
public class StringToCalcResponseDecoder extends MessageToMessageDecoder<String> {
    private static final String PREFIX = "calcResp:";
    @Override
    protected void decode(ChannelHandlerContext ctx, String msg, List<Object> out) throws Exception {
        if(msg.startsWith(PREFIX)){
            msg = msg.substring(PREFIX.length());
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
