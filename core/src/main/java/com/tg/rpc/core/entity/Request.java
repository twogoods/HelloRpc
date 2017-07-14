package com.tg.rpc.core.entity;


import lombok.Data;

import java.util.Arrays;

/**
 * Description:
 *
 * @author twogoods
 * @version 0.1
 * @since 2017-02-01
 */
@Data
public class Request {
    private long requestId;
    private Class<?> clazz;
    private String method;
    private Class<?>[] parameterTypes;
    private Object[] params;
    private long requestTime;

    public Request() {
    }
    public Request(Class<?> clazz, String method, Class<?>[] parameterTypes, Object[] params) {
        this.clazz = clazz;
        this.method = method;
        this.parameterTypes = parameterTypes;
        this.params = params;
    }
}
