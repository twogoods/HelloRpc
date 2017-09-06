package com.tg.rpc.core.exception;

/**
 * @author twogoods
 * @since 2017/7/7
 */
public class ServiceInvokeTimeOutException extends ServiceInvokeException{
    public ServiceInvokeTimeOutException(String message) {
        super(message);
    }

    public ServiceInvokeTimeOutException(String message, Throwable cause) {
        super(message, cause);
    }
}
