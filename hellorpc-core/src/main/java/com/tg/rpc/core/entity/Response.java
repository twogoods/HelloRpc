package com.tg.rpc.core.entity;

import lombok.Data;

/**
 * Description:
 *
 * @author twogoods
 * @version 0.1
 * @since 2017-02-01
 */
@Data
public class Response {
    private long requestId;
    private int code;
    private Object returnObj;
    private String msg;

    public void setStatus(ResponseStatus responseStatus) {
        this.code = responseStatus.getCode();
        this.msg = responseStatus.getMsg();
    }
}
