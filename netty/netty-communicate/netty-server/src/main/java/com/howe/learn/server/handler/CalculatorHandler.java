package com.howe.learn.server.handler;

import com.howe.learn.common.request.CalcRequest;
import com.howe.learn.common.response.CalcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @Author Karl
 * @Date 2016/10/28 10:10
 */
public class CalculatorHandler extends ChannelInboundHandlerAdapter{

    public static CalcResponse cal(CalcRequest request){
        CalcResponse response = new CalcResponse();
        response.setReqStr(request.toString());
        response.setReqId(request.getReqId());
        if(CalcRequest.CalMethod.ADDITION == request.getCalMethod()){
            response.setZ(request.getX() + request.getY());
        } else if (CalcRequest.CalMethod.SUBTRACTION == request.getCalMethod()) {
            response.setZ(request.getX() - request.getY());
        } else if (CalcRequest.CalMethod.MULTIPLICATION == request.getCalMethod()) {
            response.setZ(request.getX() * request.getY());
        } else if (CalcRequest.CalMethod.DIVISION == request.getCalMethod()) {
            response.setZ(request.getX() / request.getY());
        }
        return response;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof CalcRequest){
            CalcRequest request = (CalcRequest)msg;
            CalcResponse response = cal(request);
            ctx.channel().writeAndFlush(response);
        }
    }
}
