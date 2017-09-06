package com.tg.rpc.breaker;

/**
 * Created by twogoods on 2017/7/25.
 */
public class TestServiceIfaceImpl implements TestServiceIface {
    @Override
    public String echo(String str) {
        return "hello " + str;
    }

    @Override
    public String test(String str, int flag) {
        if (flag == 0) {
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return str;
        } else if (flag == 1) {
            throw new RuntimeException("error");
        }
        return "hello " + str;
    }
}
