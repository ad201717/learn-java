package com.howe.learn.my.rocketmq.remoting.netty;

import com.howe.learn.my.rocketmq.remoting.ChannelEventListener;
import com.howe.learn.my.rocketmq.remoting.InvokeCallback;
import com.howe.learn.my.rocketmq.remoting.RpcHook;
import com.howe.learn.my.rocketmq.remoting.common.Pair;
import com.howe.learn.my.rocketmq.remoting.common.RemotingHelper;
import com.howe.learn.my.rocketmq.remoting.common.SemaphoreReleaseOnlyOnce;
import com.howe.learn.my.rocketmq.remoting.exception.RemotingException;
import com.howe.learn.my.rocketmq.remoting.exception.RemotingSendRequestException;
import com.howe.learn.my.rocketmq.remoting.exception.RemotingTimeoutException;
import com.howe.learn.my.rocketmq.remoting.exception.RemotingTooMuchRequestException;
import com.howe.learn.my.rocketmq.remoting.protocol.RemotingCommand;
import com.howe.learn.my.rocketmq.remoting.protocol.ResponseErrorCode;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * @Author Karl
 * @Date 2017/4/11 10:20
 */
public abstract class NettyAbstractRemoting {

    private final static Logger log = LoggerFactory.getLogger(NettyAbstractRemoting.class);

    protected final Semaphore semaphoreOneway;

    protected final Semaphore semaphoreAsync;

    protected final ConcurrentHashMap<Integer, ResponseFuture> responseTable
            = new ConcurrentHashMap<>(256);

    protected Pair<NettyRequestProcessor, ExecutorService> defaultRequestProcessor;

    protected final HashMap<Integer, Pair<NettyRequestProcessor, ExecutorService>> requestProcessors = new HashMap<>();

    public abstract RpcHook getRpcHook();

    public abstract ChannelEventListener getChannelEventListener();

    protected abstract ExecutorService getCallbackExecutor();

    protected final NettyEventExecutor nettyEventExecutor = new NettyEventExecutor();

    public NettyAbstractRemoting(int permitsOneway, int permitsAsync) {
        this.semaphoreOneway = new Semaphore(permitsOneway, true);
        this.semaphoreAsync = new Semaphore(permitsAsync, true);
    }

    class NettyEventExecutor extends Thread{
        private final LinkedBlockingQueue<NettyEvent> eventQueue = new LinkedBlockingQueue<>();
        private final int MaxSize = 10000;
        private volatile boolean running = true;

        public void putEvent(NettyEvent event) {
            if (eventQueue.size() <= MaxSize) {
                eventQueue.add(event);
            } else {
                log.warn("event queue size[{}] enough, so drop this event {}", this.eventQueue.size(), event);
            }
        }

        public boolean isStop() {
            return running;
        }

        @Override
        public void run() {
            log.info(NettyEventExecutor.class.getSimpleName() + " started");
            final ChannelEventListener listener = NettyAbstractRemoting.this.getChannelEventListener();
            while(!this.isStop()){
                try {
                    NettyEvent event = this.eventQueue.poll(3000, TimeUnit.MILLISECONDS);
                    if (null != event && null != listener) {
                        switch (event.getEventType()) {
                            case CONNECT:
                                listener.onChannelConnect(event.getRemoteAddress(), event.getChannel());
                                break;
                            case CLOSE:
                                listener.onChannelClose(event.getRemoteAddress(), event.getChannel());
                                break;
                            case EXCEPTION:
                                listener.onChannelException(event.getRemoteAddress(), event.getChannel());
                                break;
                            case IDLE:
                                listener.onChannelIdle(event.getRemoteAddress(), event.getChannel());
                                break;
                            default:
                                break;
                        }
                    }
                } catch (Exception e) {
                    log.warn(NettyEventExecutor.class.getSimpleName() + " has exception", e);
                }
            }
        }
    }

    public void putEvent(NettyEvent event) {
        this.nettyEventExecutor.putEvent(event);
    }

    public void processRequestCommand(ChannelHandlerContext ctx, RemotingCommand request) {
        final Pair<NettyRequestProcessor, ExecutorService> matched = requestProcessors.get(request.getCode());
        final Pair<NettyRequestProcessor, ExecutorService> pair = null == matched ? defaultRequestProcessor : matched;

        if (null != pair) {
            Runnable runnable = () -> {
                try {
                    RpcHook rpcHook = NettyAbstractRemoting.this.getRpcHook();
                    if (null != rpcHook) {
                        rpcHook.doBeforeRequest(RemotingHelper.parseRemoteAddress(ctx.channel()), request);
                    }

                    final RemotingCommand response = pair.getObj1().processRequest(ctx, request);
                    if (null != rpcHook) {
                        rpcHook.doAfterResponse(RemotingHelper.parseRemoteAddress(ctx.channel()), request, response);
                    }

                    if (!request.isRpcOneway()) {
                        if (null != response) {
                            response.setOpaque(request.getOpaque());
                            response.markResponseType();
                            try {
                                ctx.writeAndFlush(response);
                            } catch (Throwable e) {
                                log.error("process request over, but write response fail.", e);
                                log.error(request.toString());
                                log.error(response.toString());
                            }
                        }

                    }
                } catch (Throwable e) {
                    log.error("process request error", e);
                    log.error(request.toString());
                    if (!request.isRpcOneway()) {
                        final RemotingCommand response
                                = RemotingCommand.createResponseCommand(ResponseErrorCode.SYSTEM_ERROR.getCode(), "process request error:" + e.getLocalizedMessage(), null);
                        response.setOpaque(request.getOpaque());
                        ctx.writeAndFlush(response);
                    }
                }
            };

            try {
                pair.getObj2().submit(runnable);
            } catch (RejectedExecutionException e) {
                if (System.currentTimeMillis() % 10000 == 0) {
                    log.error(RemotingHelper.parseRemoteAddress(ctx.channel())
                            + ", too many requests and thread pool busy, RejectedExecutionException " + pair.getObj2().toString()
                            + ", request code " + request.getCode());
                }
                if (!request.isRpcOneway()) {
                    final RemotingCommand response
                            = RemotingCommand.createResponseCommand(ResponseErrorCode.SYSTEM_BUSY.getCode(), "system busy", null);
                    response.setOpaque(request.getOpaque());
                    ctx.writeAndFlush(response);
                }
            }
        } else {
            log.error("unknown request code :" + request.getCode());
            final RemotingCommand response
                    = RemotingCommand.createResponseCommand(ResponseErrorCode.REQUEST_CODE_NOT_SUPPORTED.getCode(), "unknown request code :" + request.getCode(), null);
            response.setOpaque(request.getOpaque());
            ctx.writeAndFlush(response);
        }
    }

    public void processResponseCommand(ChannelHandlerContext ctx, RemotingCommand cmd) {
        final ResponseFuture responseFuture = responseTable.get(cmd.getOpaque());
        if (null != responseFuture) {
            responseFuture.setResult(cmd);

            if (null != responseFuture.getCallback()) {
                boolean runInThisThread = false;
                ExecutorService executor = this.getCallbackExecutor();
                if (null != executor) {
                    try {
                        executor.submit(() -> {
                            try {
                                responseFuture.executeCallback();
                            } catch (Exception e) {
                                log.error("execute callback error", e);
                            }
                        });
                    } catch (Exception e) {
                        log.error("submit callback error", e);
                        runInThisThread = true;
                    }
                } else {
                    runInThisThread = true;
                }

                if(runInThisThread) {
                    try {
                        responseFuture.executeCallback();
                    } catch (Exception e) {
                        log.error("execute callback error", e);
                    }
                }
            } else {
                responseFuture.putResponse(cmd);
            }
        } else {
            log.error("receive response, but not matched any request");
            log.error(cmd.toString());
        }

        responseTable.remove(cmd.getOpaque());
    }

    public void processMessageReceived(ChannelHandlerContext ctx, RemotingCommand cmd) throws Exception {
        final RemotingCommand msg = cmd;
        if (null != msg) {
            if (msg.isResponseType()) {
                processResponseCommand(ctx, msg);
            } else {
                processRequestCommand(ctx, msg);
            }
        }
    }

    protected RemotingCommand invokeSync(Channel channel, RemotingCommand request, long timeoutMills) throws RemotingSendRequestException, RemotingTimeoutException, InterruptedException {
        try {
            ResponseFuture responseFuture = new ResponseFuture(request.getOpaque(), timeoutMills, null, null);
            this.responseTable.put(request.getOpaque(), responseFuture);
            channel.writeAndFlush(request).addListener(new GenericFutureListener<Future<? super Void>>() {
                @Override
                public void operationComplete(Future<? super Void> future) throws Exception {
                    if (future.isSuccess()) {
                        responseFuture.setSendRequestOk(true);
                        return;
                    }

                    responseFuture.setSendRequestOk(false);
                    responseTable.remove(request.getOpaque());
                    responseFuture.setE(future.cause());
                    responseFuture.putResponse(null);
                    log.error("send a request command to channel<" + channel.remoteAddress() + "> failed");
                    log.error(request.toString());
                }
            });

            RemotingCommand response = responseFuture.waitResponse(timeoutMills);
            if (null == response) {
                if (responseFuture.isSendRequestOk()) {
                    throw new RemotingTimeoutException(RemotingHelper.parseRemoteAddress(channel), timeoutMills, responseFuture.getE());
                } else {
                    throw new RemotingSendRequestException(RemotingHelper.parseRemoteAddress(channel), responseFuture.getE());
                }
            }
            return response;
        } finally {
            responseTable.remove(request.getOpaque());
        }
    }


    protected void invokeAsync(Channel channel, RemotingCommand request, long timeoutMills, InvokeCallback callback)
            throws RemotingSendRequestException, RemotingTimeoutException, InterruptedException, RemotingTooMuchRequestException {
        boolean acquired = this.semaphoreAsync.tryAcquire(timeoutMills, TimeUnit.MILLISECONDS);
        if (acquired) {
            SemaphoreReleaseOnlyOnce semaphoreReleaseOnlyOnce = new SemaphoreReleaseOnlyOnce(this.semaphoreAsync);
            ResponseFuture responseFuture = new ResponseFuture(request.getOpaque(), timeoutMills, null, semaphoreReleaseOnlyOnce);
            this.responseTable.put(request.getOpaque(), responseFuture);
            try {
                channel.writeAndFlush(request).addListener(new GenericFutureListener<Future<? super Void>>() {
                    @Override
                    public void operationComplete(Future<? super Void> future) throws Exception {
                        if (future.isSuccess()) {
                            responseFuture.setSendRequestOk(true);
                            return;
                        }

                        responseFuture.setSendRequestOk(false);
                        responseTable.remove(request.getOpaque());
                        responseFuture.setE(future.cause());
                        responseFuture.putResponse(null);

                        try {
                            responseFuture.executeCallback();
                        } catch (Exception e) {
                            log.error("callback error", e);
                        } finally {
                            responseFuture.release();
                        }
                        log.error("send a request command to channel<" + channel.remoteAddress() + "> failed");
                        log.error(request.toString());
                    }
                });

            } catch (Exception e) {
                responseFuture.release();
                log.error("send request to channel<" + RemotingHelper.parseRemoteAddress(channel) + "> failed", e);
                throw new RemotingSendRequestException(RemotingHelper.parseRemoteAddress(channel), e);
            }
        } else {
            if (timeoutMills <= 0) {
                throw new RemotingTooMuchRequestException("invokeAsync invoke too fast");
            } else {
                String msg = String.format("invokeAsync tryAcquire semaphore timeout, %dms, waiting threads num:%d, semaphoreAsyncValue:%d",
                        timeoutMills, this.semaphoreAsync.getQueueLength(), this.semaphoreAsync.availablePermits());
                log.error(msg);
                log.error(request.toString());
                throw new RemotingTimeoutException(msg);
            }
        }
    }


    protected void invokeOneway(Channel channel, RemotingCommand request, long timeoutMills) throws RemotingSendRequestException, RemotingTimeoutException, InterruptedException, RemotingTooMuchRequestException {
        boolean acquired = this.semaphoreOneway.tryAcquire(timeoutMills, TimeUnit.MILLISECONDS);
        if (acquired) {
            SemaphoreReleaseOnlyOnce semaphoreReleaseOnlyOnce = new SemaphoreReleaseOnlyOnce(this.semaphoreOneway);
            ResponseFuture responseFuture = new ResponseFuture(request.getOpaque(), timeoutMills, null, semaphoreReleaseOnlyOnce);
            this.responseTable.put(request.getOpaque(), responseFuture);
            try {
                channel.writeAndFlush(request).addListener(new GenericFutureListener<Future<? super Void>>() {
                    @Override
                    public void operationComplete(Future<? super Void> future) throws Exception {
                        responseFuture.release();
                        if (!future.isSuccess()) {
                            log.error("send a request to channel<" + RemotingHelper.parseRemoteAddress(channel) + "> failed", future.cause());
                            log.error(request.toString());
                        }
                    }
                });
            } catch (Exception e) {
                responseFuture.release();
                log.error("send request to channel<" + RemotingHelper.parseRemoteAddress(channel) + "> failed", e);
                throw new RemotingSendRequestException(RemotingHelper.parseRemoteAddress(channel), e);
            }
        } else {
            if (timeoutMills <= 0) {
                throw new RemotingTooMuchRequestException("invokeOneway invoke too fast");
            } else {
                String msg = String.format("invokeOneway tryAcquire semaphore timeout, %dms, waiting threads num:%d, semaphoreAsyncValue:%d",
                        timeoutMills, this.semaphoreOneway.getQueueLength(), this.semaphoreOneway.availablePermits());
                log.error(msg);
                log.error(request.toString());
                throw new RemotingTimeoutException(msg);
            }
        }
    }


    public void scanResponseTable() {
        Iterator<Map.Entry<Integer, ResponseFuture>> iter = responseTable.entrySet().iterator();
        while(iter.hasNext()) {
            Map.Entry<Integer, ResponseFuture> it = iter.next();
            ResponseFuture resp = it.getValue();
            if (resp.getStartTimeMills() + resp.getTimeoutMills() - 1000 <= System.currentTimeMillis()) {
                iter.remove();
                try {
                    resp.executeCallback();
                } catch (Exception e) {
                    log.error("scan response table execute callback exception", e);
                } finally {
                    resp.release();
                }

                log.info("remove timeout request," + resp);
            }
        }
    }
}
