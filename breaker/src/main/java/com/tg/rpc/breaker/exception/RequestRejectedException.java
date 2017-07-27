package com.tg.rpc.breaker.exception;

/**
 * Created by twogoods on 2017/7/23.
 */
public class RequestRejectedException extends Exception {
    public RequestRejectedException(String message) {
        super(message);
    }

    public RequestRejectedException(String message, Throwable cause) {
        super(message, cause);
    }

    public RequestRejectedException(Throwable cause) {
        super(cause);
    }
}
