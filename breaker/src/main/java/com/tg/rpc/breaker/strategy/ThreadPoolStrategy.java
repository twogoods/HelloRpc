package com.tg.rpc.breaker.strategy;

/**
 * Created by twogoods on 2017/7/23.
 */
public interface ThreadPoolStrategy {
    boolean isBusy();

    void release();
}
