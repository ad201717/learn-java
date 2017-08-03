package com.howe.learn.my.rocketmq.remoting.exception;

/**
 * @Author Karl
 * @Date 2017/4/11 9:54
 */
public class RemotingException extends Exception {

    public RemotingException() {
    }

    public RemotingException(String message) {
        super(message);
    }

    public RemotingException(String message, Throwable cause) {
        super(message, cause);
    }

    public RemotingException(Throwable cause) {
        super(cause);
    }
}
