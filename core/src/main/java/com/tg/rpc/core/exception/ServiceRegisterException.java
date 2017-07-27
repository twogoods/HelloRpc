package com.tg.rpc.core.exception;

/**
 * Created by twogoods on 2017/7/27.
 */
public class ServiceRegisterException extends Exception {
    public ServiceRegisterException(String message) {
        super(message);
    }

    public ServiceRegisterException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceRegisterException(Throwable cause) {
        super(cause);
    }
}
