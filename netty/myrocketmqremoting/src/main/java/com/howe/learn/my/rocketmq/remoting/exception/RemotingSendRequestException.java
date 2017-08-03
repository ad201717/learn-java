package com.howe.learn.my.rocketmq.remoting.exception;

/**
 * @Author Karl
 * @Date 2017/4/12 14:17
 */
public class RemotingSendRequestException extends RemotingException {

    public RemotingSendRequestException(String remoteAddr) {
        this(remoteAddr, null);
    }

    public RemotingSendRequestException(String remoteAddr, Throwable cause) {
        super("send request to the channel<" + remoteAddr + "> error, " , cause);
    }
}
