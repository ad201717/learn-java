package com.howe.learn.my.rocketmq.remoting.common;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author Karl
 * @Date 2017/4/12 15:49
 */
public class SemaphoreReleaseOnlyOnce {

    private final AtomicBoolean released = new AtomicBoolean(false);
    private final Semaphore semaphore ;

    public SemaphoreReleaseOnlyOnce(Semaphore semaphore) {
        this.semaphore = semaphore;
    }

    public void release(){
        if (released.compareAndSet(false, true)) {
            semaphore.release();
        }
    }
}
