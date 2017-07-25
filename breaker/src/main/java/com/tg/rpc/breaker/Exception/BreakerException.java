package com.tg.rpc.breaker.Exception;

/**
 * Created by twogoods on 2017/7/25.
 */
public class BreakerException extends RuntimeException {
    public BreakerException(String message) {
        super(message);
    }

    public BreakerException(String message, Throwable cause) {
        super(message, cause);
    }

    public BreakerException(Throwable cause) {
        super(cause);
    }
}
