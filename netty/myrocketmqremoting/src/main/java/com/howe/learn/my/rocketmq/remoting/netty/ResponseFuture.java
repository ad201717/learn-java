package com.howe.learn.my.rocketmq.remoting.netty;

import com.howe.learn.my.rocketmq.remoting.InvokeCallback;
import com.howe.learn.my.rocketmq.remoting.common.SemaphoreReleaseOnlyOnce;
import com.howe.learn.my.rocketmq.remoting.protocol.RemotingCommand;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author Karl
 * @Date 2017/4/11 10:01
 */
public class ResponseFuture {

    private volatile RemotingCommand result;
    private volatile boolean sendRequestOk = true;
    private volatile Throwable e;
    private final InvokeCallback callback;
    private final int opaque;
    private final long startTimeMills;
    private final long timeoutMills;
    private final CountDownLatch countDownLatch = new CountDownLatch(1);
    private final AtomicBoolean executeCallbackOnce = new AtomicBoolean(false);
    private final SemaphoreReleaseOnlyOnce semaphoreReleaseOnlyOnce ;

    public ResponseFuture(int opaque, long timeoutMills, InvokeCallback callback, SemaphoreReleaseOnlyOnce semaphoreReleaseOnlyOnce) {
        this.callback = callback;
        this.opaque = opaque;
        this.timeoutMills = timeoutMills;
        this.startTimeMills = System.currentTimeMillis();
        this.semaphoreReleaseOnlyOnce = semaphoreReleaseOnlyOnce;
    }

    public RemotingCommand waitResponse(final long timeoutMills) throws InterruptedException {
        countDownLatch.await(timeoutMills, TimeUnit.MILLISECONDS);
        return this.result;
    }

    public void putResponse(final RemotingCommand result) {
        this.result = result;
        this.countDownLatch.countDown();
    }

    public void executeCallback(){
        if ( null != callback) {
            if (executeCallbackOnce.compareAndSet(false, true)) {
                callback.operationComplete(this);
            }
        }
    }

    public void release(){
        if (null != semaphoreReleaseOnlyOnce)
            semaphoreReleaseOnlyOnce.release();
    }

    public RemotingCommand getResult() {
        return result;
    }

    public void setResult(RemotingCommand result) {
        this.result = result;
    }

    public boolean isSendRequestOk() {
        return sendRequestOk;
    }

    public void setSendRequestOk(boolean sendRequestOk) {
        this.sendRequestOk = sendRequestOk;
    }

    public Throwable getE() {
        return e;
    }

    public void setE(Throwable e) {
        this.e = e;
    }

    public InvokeCallback getCallback() {
        return callback;
    }

    public int getOpaque() {
        return opaque;
    }

    public long getStartTimeMills() {
        return startTimeMills;
    }

    public long getTimeoutMills() {
        return timeoutMills;
    }
}
