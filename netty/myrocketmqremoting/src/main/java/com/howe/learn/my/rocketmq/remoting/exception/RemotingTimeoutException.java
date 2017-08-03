package com.howe.learn.my.rocketmq.remoting.exception;

/**
 * @Author Karl
 * @Date 2017/4/12 14:17
 */
public class RemotingTimeoutException extends RemotingException {

    public RemotingTimeoutException(String message) {
        super(message);
    }

    public RemotingTimeoutException(String remoteAddr, long timeoutMills) {
        this(remoteAddr, timeoutMills, null);
    }

    public RemotingTimeoutException(String remoteAddr, long timeoutMills, Throwable cause) {
        super("wait response on the channel<" + remoteAddr + "> timeout, " + timeoutMills + "ms", cause);
    }
}
