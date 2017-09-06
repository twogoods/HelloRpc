package com.tg.rpc.core.exception;

/**
 * Created by twogoods on 17/2/16.
 */
public class ValidateException extends Exception{

    public ValidateException(String message) {
        super(message);
    }

    public ValidateException(String message, Throwable cause) {
        super(message, cause);
    }
}
