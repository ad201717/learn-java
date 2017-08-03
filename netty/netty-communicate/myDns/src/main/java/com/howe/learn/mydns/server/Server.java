package com.howe.learn.mydns.server;

import com.howe.learn.mydns.server.handler.UdpHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.nio.charset.Charset;
import java.util.List;

/**
 * @Author Karl
 * @Date 2016/10/28 9:58
 */
public class Server {

    public static void main(String[] args){
        serve();
    }

    private static void serve(){
        final NioEventLoopGroup worker = new NioEventLoopGroup(4);

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioDatagramChannel.class)
                    .group(worker)
                    .handler(new ChannelInitializer<NioDatagramChannel>() {
                        @Override
                        protected void initChannel(NioDatagramChannel ch) throws Exception {
                            ch.pipeline().addLast("framer", new MessageToMessageDecoder<DatagramPacket>() {
                                @Override
                                protected void decode(ChannelHandlerContext ctx, DatagramPacket msg, List<Object> out) throws Exception {
                                    out.add(msg.content().toString(Charset.forName("UTF-8")));
                                }
                            }).addLast("handler", new UdpHandler());
                        }
                    });
            ChannelFuture future = bootstrap.bind(53).sync();
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            worker.shutdownGracefully();
        }
    }
}
