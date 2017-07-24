package com.tg.rpc.breaker.strategy;

import java.util.concurrent.Semaphore;

/**
 * Created by twogoods on 2017/7/23.
 */
public class SemaphoreStrategy implements ThreadPoolStrategy {

    private final Semaphore semaphore;

    public SemaphoreStrategy(int poolSize) {
        semaphore = new Semaphore(poolSize);
    }

    @Override
    public boolean isBusy() {
        return !semaphore.tryAcquire();
    }

    @Override
    public void release() {
        semaphore.release();
    }
}