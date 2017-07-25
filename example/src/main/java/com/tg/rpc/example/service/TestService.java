package com.tg.rpc.example.service;

/**
 * Created by twogoods on 17/2/17.
 */
public interface TestService {
    default int add(int a, int b) {
        return 0;
    }
}
