package com.howe.learn.client;

import com.howe.learn.client.handler.ResultHandler;
import com.howe.learn.common.RemoteRequest;
import com.howe.learn.common.RemoteResponse;
import com.howe.learn.common.codec.CalcRequestToStringEncoder;
import com.howe.learn.common.codec.StringToCalcResponseDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author Karl
 * @Date 2016/10/28 11:05
 */
public class ConnectionManager {

    public static ConnectionManager INSTANCE = new ConnectionManager();

    private ConnectionManager(){}

    private final ArrayBlockingQueue<Connection> connections = new ArrayBlockingQueue<Connection>(4);

    private final Set<NioEventLoopGroup> workSet = new HashSet<NioEventLoopGroup>();

    private AtomicBoolean initialed = new AtomicBoolean(false);

    private Object initLock = new Object();

    private void newConnection(){
        final NioEventLoopGroup work = new NioEventLoopGroup(1);
        final ResultHandler resultHandler = new ResultHandler();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.AUTO_READ, true)
                .group(work)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new LineBasedFrameDecoder(1024))
                                .addLast(new StringDecoder(CharsetUtil.UTF_8))
                                .addLast(new StringToCalcResponseDecoder())
                                .addLast(new LoggingHandler(LogLevel.INFO))
                                .addLast(new StringEncoder(CharsetUtil.UTF_8))
                                .addLast(new CalcRequestToStringEncoder())
                                .addLast(resultHandler);
                    }
                });
        final ChannelFuture future = bootstrap.connect("127.0.0.1", 1080);
        future.addListener(new GenericFutureListener<Future<? super Void>>() {
            public void operationComplete(Future<? super Void> f) throws Exception {
                if(future.isSuccess()){
                    Connection connection = new DefaultConnection(future.channel());
                    resultHandler.setConnection(connection);
                    workSet.add(work);
                    connections.offer(connection);
                    synchronized (initLock){
                        initLock.notifyAll();
                    }
                }else{
                    work.shutdownGracefully();
                }
            }
        });

    }

    public void init() throws InterruptedException {
        if(initialed.compareAndSet(false, true)) {
            newConnection();
            synchronized (initLock) {
                initLock.wait();
            }
        }
    }

    public Connection getConnection(){
        return connections.poll();
    }

    public void returnConnection(Connection conn){
        connections.offer(conn);
    }

    public void close() {
        for(NioEventLoopGroup work : workSet){
            work.shutdownGracefully();
        }
    }

    public static class DefaultConnection implements Connection {
        private Channel channel;
        private ConcurrentHashMap<Integer, RequestWrapper> requestMap = new ConcurrentHashMap<Integer, RequestWrapper>();

        public DefaultConnection(Channel channel){
            this.channel = channel;
        }

        public void sendAsync(RemoteRequest req, Callback callback){
            RequestWrapper reqWrapper = sendInternal(req);
            reqWrapper.callback = callback;
        }

        public RemoteResponse sendSync(RemoteRequest req) throws InterruptedException {
            RequestWrapper reqWrapper = sendInternal(req);
            return reqWrapper.get(1000);
        }

        private RequestWrapper sendInternal(RemoteRequest req){
            final RequestWrapper reqWrapper = new RequestWrapper();
            requestMap.put(reqWrapper.reqId, reqWrapper);
            req.setReqId(reqWrapper.reqId);
            channel.writeAndFlush(req).addListener(new GenericFutureListener<Future<? super Void>>() {
                public void operationComplete(Future<? super Void> future) throws Exception {
                    if(!future.isSuccess()){
                        future.cause().printStackTrace(System.err);
                        RequestWrapper failedRequest = requestMap.remove(reqWrapper.reqId);
                        //fail
                        synchronized (failedRequest) {
                            failedRequest.notifyAll();
                        }
                    }
                }
            });
            return reqWrapper;
        }

        public void result(int reqId, RemoteResponse resp){
            RequestWrapper request = requestMap.get(reqId);
            if(null != request){
                request.result = resp;
                synchronized (request) {
                    request.notifyAll();
                }
                if(null != request.callback){
                    request.callback.call(resp);
                }
            }
        }
    }

    static class RequestWrapper {
        private static AtomicInteger couter = new AtomicInteger(0);
        int reqId = couter.getAndIncrement();
        RemoteResponse result;
        Callback callback;

        public RemoteResponse get(long timeoutMS) throws InterruptedException {
            if(null != result){
                return result;
            }
            synchronized (this) {
                this.wait(timeoutMS);
            }
            return result;
        }

    }

}
