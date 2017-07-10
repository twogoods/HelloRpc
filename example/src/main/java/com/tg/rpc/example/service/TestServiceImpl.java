package com.tg.rpc.example.service;

import com.tg.rpc.springsupport.annotation.RpcService;

/**
 * Created by twogoods on 17/2/17.
 */
@RpcService
public class TestServiceImpl implements TestService {
    @Override
    public int add(int a, int b) {
        return a + b;
    }
}
