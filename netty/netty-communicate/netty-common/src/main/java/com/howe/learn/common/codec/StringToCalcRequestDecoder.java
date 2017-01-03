package com.howe.learn.common.codec;

import com.howe.learn.common.request.CalcRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * @Author Karl
 * @Date 2016/10/28 14:08
 */

/** calcReq:x + y**/
public class StringToCalcRequestDecoder extends MessageToMessageDecoder<String> {
    private static final String PREFIX = "calcReq:";

    @Override
    protected void decode(ChannelHandlerContext ctx, String msg, List<Object> out) throws Exception {
        if(msg.startsWith(PREFIX)){
            msg = msg.substring(PREFIX.length());
            String[] arrs = msg.split(":");
            CalcRequest request = new CalcRequest();
            request.setReqId(Integer.parseInt(arrs[0]));
            request.setX(Integer.parseInt(arrs[1].split(" ")[0]));
            request.setCalMethod(CalcRequest.CalMethod.from(arrs[1].split(" ")[1].toCharArray()[0]));
            request.setY(Integer.parseInt(arrs[1].split(" ")[2]));
            out.add(request);
        } else {
            throw new Exception("unknown msg:" + msg);
        }
    }
}
