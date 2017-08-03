package com.howe.learn.my.rocketmq.remoting.protocol;

/**
 * @Author Karl
 * @Date 2017/4/12 11:15
 */
public enum ResponseErrorCode {

    SUCCESS(0),
    SYSTEM_ERROR(1),
    SYSTEM_BUSY(2),
    REQUEST_CODE_NOT_SUPPORTED(3)
    ;

    int code;

    private ResponseErrorCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
