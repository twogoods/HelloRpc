package com.tg.rpc.example.service;

/**
 * Created by twogoods on 17/2/17.
 */
public interface EchoService {
    default String echo(String s) {
        return "fallback";
    }
}
