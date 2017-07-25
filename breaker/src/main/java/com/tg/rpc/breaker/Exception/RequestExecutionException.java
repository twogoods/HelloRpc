package com.tg.rpc.breaker.Exception;

/**
 * Created by twogoods on 2017/7/25.
 */
public class RequestExecutionException extends Exception{
    public RequestExecutionException(String message) {
        super(message);
    }

    public RequestExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public RequestExecutionException(Throwable cause) {
        super(cause);
    }
}
