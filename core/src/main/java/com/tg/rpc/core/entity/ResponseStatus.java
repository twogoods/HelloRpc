package com.tg.rpc.core.entity;

/**
 * Created by twogoods on 17/2/17.
 */
public enum ResponseStatus {

    SUCCESS(200, "调用成功"),
    SERVICE_NOT_FIND(404, "未发现此服务"),
    INTERNAL_ERROR(500, "内部错误");

    private int code;
    private String msg;

    ResponseStatus(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
