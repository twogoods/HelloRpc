package com.tg.rpc.breaker;

/**
 * Created by twogoods on 2017/7/25.
 */
public class TestServiceIfaceImpl implements TestServiceIface {
    @Override
    public String test(String str, int flag) {
        if(flag==1){
            try {
                Thread.sleep(10000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return str;
        }else if(flag==2){
            throw new RuntimeException("error");
        }
        return str;
    }
}
