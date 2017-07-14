package com.tg.rpc.core.exception;

/**
 * @author twogoods
 * @since 2017-07-14
 */
public class ClientMissingException extends Exception{
    public ClientMissingException(String message) {
        super(message);
    }

    public ClientMissingException(String message, Throwable cause) {
        super(message, cause);
    }
}
