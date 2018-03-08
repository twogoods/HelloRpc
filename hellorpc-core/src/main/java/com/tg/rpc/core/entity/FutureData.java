package com.tg.rpc.core.entity;

/**
 * Created by twogoods on 2018/3/8.
 */
public class FutureData<T> {
    private T t;
    private volatile boolean ready = false;

    public synchronized void setData(T t) {
        if (ready) {
            return;
        }
        this.t = t;
        ready = true;
        notifyAll();
    }

    public synchronized T get() throws InterruptedException {
        while (!ready) {
            wait();
        }
        return t;
    }
}
