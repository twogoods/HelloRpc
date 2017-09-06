package com.tg.rpc.breaker;

/**
 * Created by twogoods on 2017/7/21.
 */
public interface TestServiceIface {

    default String echo(String str) {
        return str;
    }

    default String test(String str, int flag) {
        return str;
    }
}
