package com.howe.learn.my.rocketmq.remoting.netty;

import com.howe.learn.my.rocketmq.remoting.ChannelEventListener;
import com.howe.learn.my.rocketmq.remoting.InvokeCallback;
import com.howe.learn.my.rocketmq.remoting.RemotingClient;
import com.howe.learn.my.rocketmq.remoting.RpcHook;
import com.howe.learn.my.rocketmq.remoting.common.Pair;
import com.howe.learn.my.rocketmq.remoting.common.RemotingHelper;
import com.howe.learn.my.rocketmq.remoting.exception.RemotingException;
import com.howe.learn.my.rocketmq.remoting.exception.RemotingTimeoutException;
import com.howe.learn.my.rocketmq.remoting.protocol.RemotingCommand;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author Karl
 * @Date 2017/4/12 16:14
 */
public class NettyClient extends NettyAbstractRemoting implements RemotingClient {

    private static final Logger log = LoggerFactory.getLogger(NettyClient.class);

    private final EventLoopGroup eventLoopGroupWorker;
    private DefaultEventExecutorGroup defaultEventExecutorGroup;
    private final Bootstrap bootstrap = new Bootstrap();

    private static final long lockTimeoutMills = 3000;
    private final Lock channelTableLock = new ReentrantLock();
    private final ConcurrentHashMap<String, ChannelWrapper> channelTable = new ConcurrentHashMap<>(256);
    private final Timer timer = new Timer("clientHouseKeepingService", true);

    private final ExecutorService publicExecutor;

    private final ChannelEventListener channelEventListener;

    private RpcHook rpcHook;

    public NettyClient() {
        super(NettyClientConfig.clientSemaphoreAsyncValue, NettyClientConfig.clientSemaphoreOnewayValue);

        eventLoopGroupWorker = new NioEventLoopGroup(NettyClientConfig.clientWorkerThreads, new ThreadFactory() {
            private final AtomicInteger index = new AtomicInteger(0);
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "netty client worker-" + index.getAndIncrement());
            }
        });
        publicExecutor = Executors.newFixedThreadPool(4, new ThreadFactory() {
            private AtomicInteger index = new AtomicInteger(0);
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "netty callback executor-" + index.getAndIncrement());
            }
        });
        channelEventListener = new DefaultChannelEventListener();
    }

    @Override
    public void start() {
        this.defaultEventExecutorGroup = new DefaultEventExecutorGroup(4, new ThreadFactory() {
            private AtomicInteger index = new AtomicInteger(0);
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "netty default event executor-" + index.getAndIncrement());
            }
        });
        bootstrap.group(eventLoopGroupWorker)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, false)
                .option(ChannelOption.SO_RCVBUF, 64*1000)
                .option(ChannelOption.SO_SNDBUF, 64*1000)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(defaultEventExecutorGroup,
                                new NettyDecoder(),
                                new NettyEncoder(),
                                new IdleStateHandler(0, 0, 120),
                                new NettyConnectManage(),
                                new NettyHandler());

                    }
                });
        this.timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                NettyClient.this.scanResponseTable();
            }
        }, 1000 * 3, 1000);
        if (null != this.channelEventListener) {
            this.nettyEventExecutor.start();
        }
    }

    @Override
    public void shutdown() {
        try {
            this.timer.cancel();

            for(ChannelWrapper cw : channelTable.values()) {
                this.closeChannel(cw.getChannel());
            }
            this.channelTable.clear();

            this.eventLoopGroupWorker.shutdownGracefully();
            this.defaultEventExecutorGroup.shutdownGracefully();
            this.publicExecutor.shutdown();
        } catch (Exception e) {
            log.error("netty client shutdown exception", e);
        }
    }

    @Override
    public void registerRpcHook(RpcHook rpcHook) {
        this.rpcHook = rpcHook;
    }

    @Override
    public void registerProcessor(int requestCode, NettyRequestProcessor processor, ExecutorService executorService) {
        requestProcessors.put(requestCode, new Pair<>(processor, null != executorService ? executorService : publicExecutor));
    }

//    @Override
//    public void updateNameServerList(List<String> nameServerList) {
//
//    }
//
//    @Override
//    public List<String> getNameServerList() {
//        return null;
//    }

    @Override
    public boolean isChannelWritable(String addr) {
        ChannelWrapper cw = channelTable.get(addr);
        if(null != cw && cw.isOk()) {
            return cw.isWritable();
        }
        return true;
    }

    private Channel getAndCreateChannel(String addr) throws InterruptedException {
        ChannelWrapper cw = this.channelTable.get(addr);
        if(cw != null && cw.isOk()) {
            return cw.getChannel();
        }
        return this.createChannel(addr);
    }

    private Channel createChannel(String addr) throws InterruptedException {
        ChannelWrapper cw = this.channelTable.get(addr);
        if(cw != null && cw.isOk()) {
            return cw.getChannel();
        }

        if (channelTableLock.tryLock(lockTimeoutMills, TimeUnit.MILLISECONDS)) {
            try {
                boolean createNew = false;
                cw = this.channelTable.get(addr);
                if(null != cw) {
                    if (cw.isOk()) {
                        return cw.getChannel();
                    } else if (!cw.channelFuture.isDone()) {

                    } else {
                        this.channelTable.remove(addr);
                        createNew = true;
                    }
                } else {
                    createNew = true;
                }

                if (createNew) {
                    String ip = addr.substring(0, addr.indexOf(":"));
                    String port = addr.substring(addr.indexOf(":") + 1);
                    ChannelFuture cf = bootstrap.connect(InetSocketAddress.createUnresolved(ip, Integer.parseInt(port)));
                    this.channelTable.put(addr, new ChannelWrapper(cf));
                }
            } catch (Exception e) {
                log.error("createChannel exception", e);
            } finally {
                channelTableLock.unlock();
            }
        } else {
            log.error("createChannel try to lock table timeout {}", lockTimeoutMills);
        }

        if (cw != null) {
            ChannelFuture cf = cw.channelFuture;
            if (cf.awaitUninterruptibly(NettyClientConfig.clientConnectTimeoutMills, TimeUnit.MILLISECONDS)) {
                if (cw.isOk()) {
                    log.info("createChannel <{}> success", addr);
                    return cw.getChannel();
                } else {
                    log.error("createChannel <" + addr + "> failed", cf.cause());
                }
            }
        }

        return null;
    }

    @Override
    public RemotingCommand invokeSync(String address, RemotingCommand request, long timeoutMills) throws RemotingException, InterruptedException {
        Channel channel = this.getAndCreateChannel(address);
        return this.invokeSync(channel, request, timeoutMills);
    }

    @Override
    public void invokeAsync(String address, RemotingCommand request, long timeoutMills, InvokeCallback invokeCallback) throws RemotingException, InterruptedException {
        Channel channel = this.getAndCreateChannel(address);
        this.invokeAsync(channel, request, timeoutMills, invokeCallback);
    }

    @Override
    public void invokeOneway(String address, RemotingCommand request, long timeoutMills) throws RemotingException, InterruptedException {
        Channel channel = this.getAndCreateChannel(address);
        this.invokeOneway(channel, request, timeoutMills);
    }

    @Override
    public RpcHook getRpcHook() {
        return this.rpcHook;
    }

    @Override
    public ChannelEventListener getChannelEventListener() {
        return this.channelEventListener;
    }

    @Override
    protected ExecutorService getCallbackExecutor() {
        return this.publicExecutor;
    }

    class ChannelWrapper {
        private final ChannelFuture channelFuture;

        public ChannelWrapper(ChannelFuture channelFuture) {
            this.channelFuture = channelFuture;
        }

        public boolean isOk(){
            return null != this.channelFuture.channel() && this.channelFuture.channel().isActive();
        }

        public boolean isWritable(){
            return this.channelFuture.channel().isWritable();
        }

        public Channel getChannel() {
            return channelFuture.channel();
        }

        public ChannelFuture getChannelFuture() {
            return channelFuture;
        }
    }

    class NettyHandler extends SimpleChannelInboundHandler<RemotingCommand> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, RemotingCommand msg) throws Exception {
            processMessageReceived(ctx, msg);
        }
    }

    class NettyConnectManage extends ChannelDuplexHandler {
        @Override
        public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise future) throws Exception {
            log.info("NETTY CLIENT PIPELINE : CONNECT {} => {}", localAddress.toString(), remoteAddress.toString());
            super.connect(ctx, remoteAddress, localAddress, future);
            if (null != NettyClient.this.getChannelEventListener()) {
                NettyClient.this.putEvent(new NettyEvent(NettyEventType.CONNECT, remoteAddress.toString(), ctx.channel()));
            }
        }

        @Override
        public void disconnect(ChannelHandlerContext ctx, ChannelPromise future) throws Exception {
            log.info("NETTY CLIENT PIPELINE : DISCONNECT {}", RemotingHelper.parseRemoteAddress(ctx.channel()));
            super.disconnect(ctx, future);
            if (null != NettyClient.this.getChannelEventListener()) {
                NettyClient.this.putEvent(new NettyEvent(NettyEventType.CLOSE, RemotingHelper.parseRemoteAddress(ctx.channel()), ctx.channel()));
            }
        }

        @Override
        public void close(ChannelHandlerContext ctx, ChannelPromise future) throws Exception {
            log.info("NETTY CLIENT PIPELINE : CLOSE {}", RemotingHelper.parseRemoteAddress(ctx.channel()));
            super.close(ctx, future);
            if (null != NettyClient.this.getChannelEventListener()) {
                NettyClient.this.putEvent(new NettyEvent(NettyEventType.CLOSE, RemotingHelper.parseRemoteAddress(ctx.channel()), ctx.channel()));
            }
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof IdleStateEvent) {
                IdleStateEvent event = (IdleStateEvent) evt;
                if (event.state().equals(IdleState.ALL_IDLE)) {
                    String remoteAddr = RemotingHelper.parseRemoteAddress(ctx.channel());
                    log.info("NETTY CLIENT PIPELINE : ALL_IDLE {}", remoteAddr);
                    closeChannel(ctx.channel());
                }
            }
            super.userEventTriggered(ctx, evt);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            String remoteAddr = RemotingHelper.parseRemoteAddress(ctx.channel());
            log.error("NETTY CLIENT PIPELINE : EXCEPTION {}", remoteAddr);
            log.error("NETTY CLIENT PIPELINE : EXCEPTION cause.", cause);
            super.exceptionCaught(ctx, cause);
            if (null != NettyClient.this.getChannelEventListener()) {
                NettyClient.this.putEvent(new NettyEvent(NettyEventType.EXCEPTION, RemotingHelper.parseRemoteAddress(ctx.channel()), ctx.channel()));
            }
        }
    }

    public void closeChannel(Channel channel) {
        if (null == channel) {
            return;
        }
        try {
            if (this.channelTableLock.tryLock(lockTimeoutMills, TimeUnit.MILLISECONDS)) {
                try {
                    boolean remove = false;
                    String addr = null;
                    ChannelWrapper cw = null;
                    for (String key : channelTable.keySet()) {
                        ChannelWrapper prev = channelTable.get(key);
                        if (null != prev.getChannel() && prev.getChannel() == channel) {
                            cw = prev;
                            addr = key;
                            remove = true;
                        }
                    }

                    if (null == cw) {
                        log.warn("eventCloseChannel: the channel<{}> has been removed before", RemotingHelper.parseRemoteAddress(channel));
                    }

                    if (remove) {
                        channelTable.remove(addr);
                        log.info("close channel: the channel<{}> is moved from channel table", RemotingHelper.parseRemoteAddress(channel));
                        RemotingHelper.closeChannel(channel);
                    }
                } catch (Exception e) {
                    log.error("close channel exception", e);
                } finally {
                    this.channelTableLock.unlock();
                }
            } else {
                log.error("close channel : try to lock channel table timeout {} ms", lockTimeoutMills);
            }
        } catch (Exception e) {
            log.error("close channel exception", e);
        }

    }

    class DefaultChannelEventListener implements ChannelEventListener {

        @Override
        public void onChannelConnect(String remoteAddress, Channel channel) {

        }

        @Override
        public void onChannelClose(String remoteAddress, Channel channel) {

        }

        @Override
        public void onChannelException(String remoteAddress, Channel channel) {

        }

        @Override
        public void onChannelIdle(String remoteAddress, Channel channel) {

        }
    }
}
