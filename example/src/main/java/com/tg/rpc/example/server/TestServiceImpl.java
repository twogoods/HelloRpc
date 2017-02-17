package com.tg.rpc.example.server;

/**
 * Created by twogoods on 17/2/17.
 */
public class TestServiceImpl implements TestService {
    @Override
    public int add(int a, int b) {
        return a+b;
    }
}
