package com.tg.rpc.core.entity;

/**
 * Description:
 *
 * @author twogoods
 * @version 0.1
 * @since 2017-02-01
 */
public class Response {
    private long requestId;
    private int code;
    private Object returnObj;

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getReturnObj() {
        return returnObj;
    }

    public void setReturnObj(Object returnObj) {
        this.returnObj = returnObj;
    }

    @Override
    public String toString() {
        return "Response{" +
                "requestId=" + requestId +
                ", code=" + code +
                ", returnObj=" + returnObj +
                '}';
    }
}
