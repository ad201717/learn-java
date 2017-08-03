package com.howe.learn.my.rocketmq.remoting.protocol;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.howe.learn.my.rocketmq.remoting.common.RemotingSerialize;
import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author Karl
 * @Date 2017/4/11 9:42
 */
public class RemotingCommand {

    private static final AtomicInteger RequestID = new AtomicInteger(0);

    private int opaque = RequestID.incrementAndGet();

    private int code;

    private String remark;

    private int flag = 0;

    private Map<String, String> headers;

    private transient byte[] body;

    private static final int RPC_TYPE = 0;
    private static final int RPC_ONEWAY = 1;

    protected RemotingCommand(){}

    public static RemotingCommand createRequestCommand(int code, Map<String, String> headers) {
        RemotingCommand cmd = new RemotingCommand();
        cmd.setCode(code);
        cmd.setHeaders(headers);
        return cmd;
    }

    public static RemotingCommand createResponseCommand(int code, String remark, Map<String, String> headers) {
        RemotingCommand cmd = new RemotingCommand();
        cmd.markResponseType();
        cmd.setCode(code);
        cmd.setRemark(remark);
        cmd.setHeaders(headers);
        return cmd;
    }

    /**
     * 序列化格式如下
     * |----|----|------------|---------------------------|
     * |total-length|header-length|header|body|
     * @return
     */
    public ByteBuffer encode(){
        int length = 4;
        byte[] headerBytes = RemotingSerialize.encode(this);
        length += headerBytes.length;

        if (null != body) {
            length += body.length;
        }

        ByteBuffer buf = ByteBuffer.allocate(length);
        buf.putInt(length);
        buf.putInt(headerBytes.length);
        buf.put(headerBytes);
        if (null != body) {
            buf.put(body);
        }
        buf.flip();
        return buf;
    }

    public static RemotingCommand decode(byte[] bytes) {
        return decode(ByteBuffer.wrap(bytes));
    }

    public static RemotingCommand decode(ByteBuffer buffer){
        int length = buffer.limit();
        int headerLen = buffer.getInt();

        byte[] headerBytes = new byte[headerLen];
        buffer.get(headerBytes);

        int bodyLen = length - 4 - headerLen;
        byte[] bodyData = null;
        if (bodyLen > 0) {
            bodyData = new byte[bodyLen];
            buffer.get(bodyData);
        }

        RemotingCommand cmd = RemotingSerialize.decode(headerBytes, RemotingCommand.class);
        cmd.body = bodyData;

        return cmd;
    }

    public void markResponseType(){
        int bits = 1 << RPC_TYPE;
        this.flag |= bits;
    }

    @JSONField(serialize = false)
    public boolean isResponseType(){
        int bits = 1 << RPC_TYPE;
        return (this.flag & bits) == bits;
    }

    public void markRpcOneway(){
        int bits = 1 << RPC_ONEWAY;
        this.flag |= bits;
    }

    @JSONField(serialize = false)
    public boolean isRpcOneway(){
        int bits = 1 << RPC_ONEWAY;
        return (this.flag & bits) == bits;
    }

    public int getOpaque() {
        return opaque;
    }

    public void setOpaque(int opaque) {
        this.opaque = opaque;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("RemotingCommand{");
        sb.append("opaque=").append(opaque);
        sb.append(", code=").append(code);
        sb.append(", remark=").append(remark);
        sb.append(", flag=").append(Integer.toBinaryString(flag));
        sb.append(", headers=").append(headers);
        sb.append('}');
        return sb.toString();
    }
}
