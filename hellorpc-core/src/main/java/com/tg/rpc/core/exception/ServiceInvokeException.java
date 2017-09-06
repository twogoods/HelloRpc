package com.tg.rpc.core.exception;

/**
 * Created by twogoods on 17/2/17.
 */
public class ServiceInvokeException extends Exception{
    public ServiceInvokeException(String message) {
        super(message);
    }

    public ServiceInvokeException(String message, Throwable cause) {
        super(message, cause);
    }
}
