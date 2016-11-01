package com.howe.learn.client;

import com.howe.learn.common.RemoteResponse;

/**
 * @Author Karl
 * @Date 2016/10/28 11:12
 */
public interface Callback {

    void call(RemoteResponse resp) ;
}
