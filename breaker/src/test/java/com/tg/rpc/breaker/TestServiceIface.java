package com.tg.rpc.breaker;

/**
 * Created by twogoods on 2017/7/21.
 */
public interface TestServiceIface {
    default String test(String str) {
        return str;
    }
}
