package com.howe.learn.my.rocketmq.remoting.exception;

/**
 * @Author Karl
 * @Date 2017/4/12 14:17
 */
public class RemotingConnectException extends RemotingException {

    public RemotingConnectException(String remoteAddr) {
        this(remoteAddr, null);
    }

    public RemotingConnectException(String remoteAddr, Throwable cause) {
        super("connect to the channel<" + remoteAddr + "> error, " , cause);
    }
}
