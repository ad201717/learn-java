package com.howe.learn.client;

import com.howe.learn.common.RemoteRequest;
import com.howe.learn.common.RemoteResponse;

/**
 * @Author Karl
 * @Date 2016/11/1 14:53
 */
public interface Connection {

    void sendAsync(RemoteRequest req, Callback callback);

    RemoteResponse sendSync(RemoteRequest req) throws InterruptedException ;

    void result(int reqId, RemoteResponse resp);
}
