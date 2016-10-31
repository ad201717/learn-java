package com.howe.learn.server;

import com.howe.learn.common.codec.CalcResponseToStringEncoder;
import com.howe.learn.common.codec.StringToCalcRequestDecoder;
import com.howe.learn.server.handler.CalculatorHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.CharsetUtil;

/**
 * @Author Karl
 * @Date 2016/10/28 9:58
 */
public class Server {

    public static void main(String[] args){
        serve();
    }

    private static void serve(){
        NioEventLoopGroup boss = new NioEventLoopGroup(1);
        NioEventLoopGroup work = new NioEventLoopGroup(4);
        try {
            ServerBootstrap server = new ServerBootstrap();
            server.option(ChannelOption.SO_REUSEADDR, true)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .group(boss, work)
                    .childOption(ChannelOption.AUTO_READ, true)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new LineBasedFrameDecoder(1024))
                                    .addLast(new StringDecoder(CharsetUtil.UTF_8))
                                    .addLast(new StringToCalcRequestDecoder())
                                    .addLast(new StringEncoder(CharsetUtil.UTF_8))
                                    .addLast(new CalcResponseToStringEncoder())
                                    .addLast(new LoggingHandler(LogLevel.DEBUG))
                                    .addLast(new CalculatorHandler());

                        }
                    }).bind(1080)
            .sync().channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            boss.shutdownGracefully();
            work.shutdownGracefully();
        }
    }
}
