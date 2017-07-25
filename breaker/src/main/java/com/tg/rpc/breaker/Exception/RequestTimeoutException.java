package com.tg.rpc.breaker.Exception;

/**
 * Created by twogoods on 2017/7/25.
 */
public class RequestTimeoutException extends Exception{
    public RequestTimeoutException(String message) {
        super(message);
    }

    public RequestTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    public RequestTimeoutException(Throwable cause) {
        super(cause);
    }
}
