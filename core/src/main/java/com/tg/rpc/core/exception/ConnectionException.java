package com.tg.rpc.core.exception;

/**
 * Description:
 * @author twogoods
 * @version 0.1
 * @since 2017-04-12
 */
public class ConnectionException extends Exception{
    public ConnectionException(String message) {
        super(message);
    }

    public ConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
