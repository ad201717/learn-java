package com.howe.learn.common.response;

import com.howe.learn.common.RemoteResponse;

/**
 * @Author Karl
 * @Date 2016/10/28 10:17
 */
public class CalcResponse extends RemoteResponse{
    private String reqStr;

    private int z;

    public String getReqStr() {
        return reqStr;
    }

    public void setReqStr(String reqStr) {
        this.reqStr = reqStr;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    @Override
    public String toString() {
        return reqStr + "=" + z;
    }
}
