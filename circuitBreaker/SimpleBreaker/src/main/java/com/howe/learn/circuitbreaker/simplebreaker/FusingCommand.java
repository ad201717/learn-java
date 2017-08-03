package com.howe.learn.circuitbreaker.simplebreaker;

import com.google.common.base.Optional;

/**
 * @Author Karl
 * @Date 2017/7/21 13:59
 */
public abstract class FusingCommand<T> {



    protected abstract Optional<T> run() ;

    protected Optional<T> getFallback(boolean isFusing, Exception e){
        return Optional.absent();
    }
}
