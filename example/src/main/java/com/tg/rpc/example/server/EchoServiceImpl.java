package com.tg.rpc.example.server;


/**
 * Created by twogoods on 17/2/17.
 */
public class EchoServiceImpl implements EchoService {
    @Override
    public String hello(String s) {
        return s;
    }
}
