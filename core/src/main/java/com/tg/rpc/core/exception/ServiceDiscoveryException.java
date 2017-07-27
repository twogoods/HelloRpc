package com.tg.rpc.core.exception;

/**
 * Created by twogoods on 2017/7/27.
 */
public class ServiceDiscoveryException extends Exception{
    public ServiceDiscoveryException(String message) {
        super(message);
    }

    public ServiceDiscoveryException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceDiscoveryException(Throwable cause) {
        super(cause);
    }
}
